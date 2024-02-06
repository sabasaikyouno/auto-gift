package twitter

import csv.VariousCSV.{getFollowing, getNoFollowing, writeNoFollowing}
import org.joda.time.{DateTime, LocalDate, Seconds}
import org.openqa.selenium.chrome.ChromeDriver
import twitter.GetTweets.getTweets
import twitter.TwitterActions.{change_account, follow, likeAndRepost}
import twitter.TwitterInit.initTwitter

import scala.collection.immutable.HashSet
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Twitter {
  private val followingHashSet = mutable.HashSet(getFollowing: _*)
  private val myAccountsCookie = mutable.HashSet[String]()

  def startAutoGift() = {
    val (chrome, chrome2) = initTwitter(followingHashSet, myAccountsCookie)

    autoGiftLoop(new LocalDate().minusDays(1).toString, new DateTime().plusDays(1))(chrome, chrome2)
  }

  def autoGiftLoop(lastDateTime: String, nextTime: DateTime)(chrome: ChromeDriver, chrome2: ChromeDriver): Unit = {
    val tweets = getTweets(lastDateTime)(chrome)
    val noFollowList = tweets.map(_._2).diff(followingHashSet.toSet) ++ getNoFollowing

    writeNoFollowing(noFollowList.drop(100))

    Future(startFollow(noFollowList.take(100))(chrome2))

    startLikeAndRepost(tweets)(chrome)

    Thread.sleep(Seconds.secondsBetween(new DateTime(), nextTime).getSeconds.toLong * 1000)
    autoGiftLoop(nextTime.minusDays(1).toString, new DateTime().plusDays(1))(chrome, chrome2)
  }

  private def startFollow(noFollowList: HashSet[String])(implicit chrome: ChromeDriver) = {
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

  private def startLikeAndRepost(tweets: HashSet[(String, String)])(implicit chrome: ChromeDriver) = {
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
