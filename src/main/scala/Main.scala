import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
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
  private val myAccountsCookie = mutable.HashSet[String]()

  def main(args: Array[String]) = {
    val options = new ChromeOptions()
    options.addArguments("--headless=new")
    options.addArguments("--disable-gpu")
    options.addArguments("--window-size=945,1020")

    val chrome: ChromeDriver = new ChromeDriver(options)
    chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))

    val chrome2: ChromeDriver = new ChromeDriver(options)
    chrome2.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))

    scala.sys.addShutdownHook {
      writeFollowing(followingHashSet)
    }

    getAccounts.foreach { case (id, pass) =>
      login(id, pass)(chrome)
      myAccountsCookie += chrome.manage().getCookieNamed("auth_token").getValue
    }

    getAccounts.foreach { case (id, pass) =>
      login(id, pass)(chrome2)
    }
    
    start(new LocalDate().minusDays(1).toString, new DateTime().plusDays(1))(chrome, chrome2)
  }

  def start(lastDateTime: String, nextTime: DateTime)(chrome: ChromeDriver, chrome2: ChromeDriver): Unit = {
    val tweets = getTweets(lastDateTime)(chrome)
    val noFollowList = tweets.map(_._2).diff(followingHashSet.toSet) ++ getNoFollowing

    writeNoFollowing(noFollowList.drop(100))

    Future(startFollow(noFollowList.take(100))(chrome2))

    startLikeAndRepost(tweets)(chrome)

    Thread.sleep(Seconds.secondsBetween(new DateTime(), nextTime).getSeconds.toLong * 1000)
    start(nextTime.minusDays(1).toString, new DateTime().plusDays(1))(chrome, chrome2)
  }

  def startFollow(noFollowList: HashSet[String])(implicit chrome: ChromeDriver) = {
    var nextFollowTime = new DateTime().plusMinutes(30)

    noFollowList.grouped(15).foreach { noFollows =>
      myAccountsCookie.foreach { authToken =>
        change_account(authToken)
        noFollows.foreach(follow)
      }
      noFollows.foreach(followingHashSet += _)

      Thread.sleep(Seconds.secondsBetween(new DateTime(), nextFollowTime).getSeconds * 1000)
      nextFollowTime = new DateTime().plusMinutes(30)
    }
  }

  def startLikeAndRepost(tweets: HashSet[(String, String)])(implicit chrome: ChromeDriver) = {
    var nextLikeAndRepostTime = new DateTime().plusMinutes(15)

    tweets.grouped(40).foreach { tweetList =>
      myAccountsCookie.foreach{ authToken =>
        change_account(authToken)
        tweetList.foreach{case (url, _) => likeAndRepost(url)}
      }

      Thread.sleep(Seconds.secondsBetween(new DateTime(), nextLikeAndRepostTime).getSeconds * 1000)
      nextLikeAndRepostTime = new DateTime().plusMinutes(15)
    }
  }
}
