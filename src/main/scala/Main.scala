import org.openqa.selenium.chrome.{ChromeDriver}
import java.time.Duration

import GetTweets.getTweets
import TwitterActions.{change_account, follow, likeAndRepost}
import TwitterLogin.login
import VariousCSV.{getAccounts, getFollowing, getNoFollowing, writeFollowing, writeNoFollowing}
import org.joda.time.{DateTime, LocalDate, Seconds}

import scala.collection.immutable.HashSet
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main {
  private val followingHashSet = mutable.HashSet(getFollowing: _*)

  def main(args: Array[String]) = {
    val chrome: ChromeDriver = new ChromeDriver()
    chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))

    val chrome2: ChromeDriver = new ChromeDriver()
    chrome2.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))

    scala.sys.addShutdownHook {
      writeFollowing(followingHashSet)
    }

    getAccounts.foreach { case (id, pass) =>
      login(id, pass)(chrome)
    }

    getAccounts.foreach { case (id, pass) =>
      login(id, pass)(chrome2)
    }
    
    start(new LocalDate().minusDays(2).toString, new DateTime().plusDays(1))(chrome, chrome2)
  }

  def start(lastDateTime: String, nextTime: DateTime)(chrome: ChromeDriver, chrome2: ChromeDriver): Unit = {
    val tweets = getTweets(lastDateTime)(chrome)
    val noFollowList = tweets.map(_._2).diff(followingHashSet.toSet) ++ getNoFollowing
    val myAccounts = getAccounts

    writeNoFollowing(noFollowList.drop(100))

    Future(startFollow(noFollowList.take(100), myAccounts)(chrome2))

    startLikeAndRepost(tweets, myAccounts)(chrome)

    Thread.sleep(Seconds.secondsBetween(new DateTime(), nextTime).getSeconds.toLong * 1000)
    start(new DateTime().toString, new DateTime().plusDays(1))(chrome, chrome2)
  }

  def startFollow(noFollowList: HashSet[String], myAccounts: List[(String, String)])(implicit chrome: ChromeDriver) = {
    val nextFollowTime = new DateTime().plusMinutes(30)

    noFollowList.grouped(15).foreach { noFollows =>
      myAccounts.foreach{case (myId, _) =>
        change_account(myId)
        noFollows.foreach(follow)
      }
      noFollows.foreach(followingHashSet += _)

      Thread.sleep(Seconds.secondsBetween(new DateTime(), nextFollowTime).getSeconds * 1000)
    }
  }

  def startLikeAndRepost(tweets: HashSet[(String, String)], myAccounts:  List[(String, String)])(implicit chrome: ChromeDriver) = {
    val nextLikeAndRepostTime = new DateTime().plusMinutes(15)

    tweets.grouped(40).foreach { tweetList =>
      myAccounts.foreach{case (myId, _) =>
        change_account(myId)
        tweetList.foreach{case (url, _) => likeAndRepost(url)}
      }

      Thread.sleep(Seconds.secondsBetween(new DateTime(), nextLikeAndRepostTime).getSeconds * 1000)
    }
  }
}
