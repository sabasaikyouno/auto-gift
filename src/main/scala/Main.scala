import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import java.time.Duration

import GetTweets.getTweets
import TwitterActions.{change_account, follow, likeAndRepost}
import VariousCSV.{followingWrite, getAccounts, getFollowing}
import org.joda.time.{DateTime, LocalDate, Seconds}

import scala.collection.mutable

object Main {
  private val followingHashSet = mutable.HashSet(getFollowing: _*)

  def main(args: Array[String]) = {
    System.setProperty("webdriver.chrome.driver", "chrome\\chromedriver_win32\\chromedriver.exe")
    val options = new ChromeOptions()
    options.addArguments("--user-data-dir=\\chrome")
    options.addArguments("--profile-directory=Profile1")

    implicit val chrome: ChromeDriver = new ChromeDriver(options)
    chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))

    scala.sys.addShutdownHook {
      followingWrite(followingHashSet)
    }

    loop(new LocalDate().minusDays(2).toString, new DateTime().plusDays(1))
  }

  def loop(lastDateTime: String, nextTime: DateTime)(implicit chrome: ChromeDriver): Unit = {
    val tweets = getTweets(lastDateTime)
    val noFollowList = tweets.map(_._2).diff(followingHashSet.toSet)
    val myAccounts = getAccounts

    noFollowList.grouped(15).foreach { noFollows =>
      myAccounts.foreach{case (myId, _) =>
        change_account(myId)
        noFollows.foreach(follow)
      }
      noFollows.foreach(followingHashSet += _)
      Thread.sleep(1800)
    }

    tweets.grouped(14).foreach { tweetList =>
      myAccounts.foreach{case (myId, _) =>
        change_account(myId)
        tweetList.foreach{case (url, _) => likeAndRepost(url)}
      }
      Thread.sleep(900)
    }

    Thread.sleep(Seconds.secondsBetween(new DateTime(), nextTime).getSeconds)
    loop(new DateTime().toString, new DateTime().plusDays(1))
  }
}
