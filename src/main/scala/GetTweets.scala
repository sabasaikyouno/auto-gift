import org.joda.time.DateTime
import org.openqa.selenium.{By, JavascriptExecutor}
import org.openqa.selenium.chrome.ChromeDriver

import scala.collection.immutable.HashSet
import scala.jdk.CollectionConverters._

object GetTweets {
  def getTweets(lastDateTimeStr: String)(implicit chrome: ChromeDriver) = {
    chrome.get(searchUrl)
    Thread.sleep(3000)
    val lastDateTime = DateTime.parse(lastDateTimeStr)
    getTweetsList(HashSet[(String, String)](), lastDateTime)
  }

  private def getTweetsList(tweetsHash: HashSet[(String, String)], lastDateTime: DateTime)(implicit chrome: ChromeDriver): HashSet[(String, String)] = {
    val tweetsEle = chrome
      .findElement(By.xpath("//div[@aria-label='タイムライン: タイムラインを検索']"))
      .findElements(By.cssSelector(".css-1rynq56.r-bcqeeo.r-qvutc0.r-1tl8opc.r-a023e6.r-rjixqe.r-16dba41.r-xoduu5.r-1q142lx.r-1w6e6rj.r-9aw3ui.r-3s2u2q.r-1loqt21"))
      .asScala

    val tweets = tweetsEle
      .map { ele =>
        val url = ele.getAttribute("href")
        val userId = url.drop(20).split('/').head
        (url, userId)
      }.toSet

    val nextTweets = tweetsHash ++ tweets
    val dateTimeList = tweetsEle.map( ele => DateTime.parse(ele.findElement(By.tagName("time")).getAttribute("datetime")))

    if (dateTimeList.exists(_.isAfter(lastDateTime)) && nextTweets.size <= 1000) {
      println(tweets)
      val jsExecutor = chrome.asInstanceOf[JavascriptExecutor]
      jsExecutor.executeScript("window.scrollBy(0, 500);")
      Thread.sleep(1000) // スクロールが完了するまで待機
      getTweetsList(nextTweets, lastDateTime)
    } else {
      tweetsHash
    }
  }

  //抽選　応募　参加方法　参加条件　プレゼントのOR、200RT以上、指定した日付まで検索
  private def searchUrl = {
    s"https://twitter.com/search?q=%E6%8A%BD%E9%81%B8%20OR%20%E5%BF%9C%E5%8B%9F%20OR%20%E5%8F%82%E5%8A%A0%E6%96%B9%E6%B3%95%20OR%20%E5%8F%82%E5%8A%A0%E6%9D%A1%E4%BB%B6%20OR%20%E3%83%97%E3%83%AC%E3%82%BC%E3%83%B3%E3%83%88%20min_retweets%3A300&src=typed_query&f=live"
  }
}
