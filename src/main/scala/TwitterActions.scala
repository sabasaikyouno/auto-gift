import org.openqa.selenium.{By, Cookie, Keys}
import org.openqa.selenium.chrome.ChromeDriver

import scala.util.Try

object TwitterActions {
  def likeAndRepost(url: String)(implicit chrome: ChromeDriver): Unit = {
    Try {
      chrome.get(url)
      //要素が要素が読み込まれるまで待つ
      chrome.findElement(By.xpath("//section[@aria-labelledby='accessible-list-0']"))

      like()
      repost()
    }.getOrElse(())
  }

  //フォロー
  def follow(id: String)(implicit chrome: ChromeDriver)  = {
    val url = s"https://twitter.com/$id"
    chrome.get(url)

    Try{
      chrome.findElement(By.cssSelector(".css-175oi2r.r-sdzlij.r-1phboty.r-rs99b7.r-lrvibr.r-2yi16.r-1qi8awa.r-ymttw5.r-o7ynqc.r-6416eg.r-1ny4l3l")).click()
    }.getOrElse(())
  }

  def change_account(authToken: String)(implicit chrome: ChromeDriver) = {
    chrome.manage().deleteCookieNamed("auth_token")
    chrome.manage().addCookie(new Cookie("auth_token", authToken))
    chrome.get("https://twitter.com/home")
    Thread.sleep(1500)
  }

  //いいねする
  private def like()(implicit chrome: ChromeDriver): Unit = {
    chrome.switchTo().activeElement().sendKeys("l")
  }

  //リポストする
  private def repost()(implicit chrome: ChromeDriver) = {
    chrome.switchTo().activeElement().sendKeys("t")
    Thread.sleep(500)
    chrome.switchTo().activeElement().sendKeys(Keys.ENTER)
  }
}