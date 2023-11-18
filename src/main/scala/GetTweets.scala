import org.openqa.selenium.{By, JavascriptExecutor}
import org.openqa.selenium.chrome.ChromeDriver

import scala.collection.mutable
import scala.jdk.CollectionConverters._

object GetTweets {
  def getTweets(lastDateTime: String)(implicit chrome: ChromeDriver) = {
    chrome.get(makeSearchUrl(lastDateTime))
    Thread.sleep(5000)

    val tweets = mutable.HashSet[(String, String)]()

    val jsExecutor = chrome.asInstanceOf[JavascriptExecutor]

    //一番下まで行くか、1000ツイート読み込んだら終了
    while (!isScrollbarAtBottom && tweets.size <= 1000) {
      jsExecutor.executeScript("window.scrollBy(0, 500);")
      Thread.sleep(1000) // スクロールが完了するまで待機
      chrome
        .findElement(By.xpath("//div[@aria-label='タイムライン: タイムラインを検索']"))
        .findElements(By.cssSelector(".css-4rbku5.css-18t94o4.css-901oao.r-14j79pv.r-1loqt21.r-xoduu5.r-1q142lx.r-1w6e6rj.r-1tl8opc.r-a023e6.r-16dba41.r-9aw3ui.r-rjixqe.r-bcqeeo.r-3s2u2q.r-qvutc0"))
        .asScala
        .foreach { ele =>
          val url = ele.getAttribute("href")
          val userId = url.drop(20).split('/').head
          tweets += ((url, userId))
        }
    }

    tweets.toList
  }

  //スクロールできるかどうか
  private def isScrollbarAtBottom(implicit chrome:ChromeDriver): Boolean = {
    val jsExecutor = chrome.asInstanceOf[JavascriptExecutor]
    val windowHeight = jsExecutor.executeScript("return window.innerHeight;").asInstanceOf[Long]
    val documentHeight = jsExecutor.executeScript("return document.body.scrollHeight;").asInstanceOf[Long]
    val scrollPosition = jsExecutor.executeScript("return window.scrollY;").asInstanceOf[Long]

    // スクロール位置 + ウィンドウの高さ が ドキュメントの高さと等しいかどうか
    scrollPosition + windowHeight == documentHeight
  }

  //抽選　応募　参加方法　参加条件　プレゼントのOR、200RT以上、指定した日付まで検索
  private def makeSearchUrl(lastDateTime: String) = {
    s"https://twitter.com/search?q=%E6%8A%BD%E9%81%B8%20OR%20%E5%BF%9C%E5%8B%9F%20OR%20%E5%8F%82%E5%8A%A0%E6%96%B9%E6%B3%95%20OR%20%E5%8F%82%E5%8A%A0%E6%9D%A1%E4%BB%B6%20OR%20%E3%83%97%E3%83%AC%E3%82%BC%E3%83%B3%E3%83%88%20min_retweets%3A300%20since%3A$lastDateTime&src=typed_query&f=live"
  }
}
