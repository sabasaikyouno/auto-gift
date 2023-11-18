import org.openqa.selenium.{By, Keys}
import org.openqa.selenium.chrome.ChromeDriver

object TwitterActions {
  def likeAndRepost(url: String)(implicit chrome: ChromeDriver): Unit = {
    chrome.get(url)
    Thread.sleep(5000)

    like()
    repost()
  }

  //フォロー
  def follow(url: String)(implicit chrome: ChromeDriver)  = {
    chrome.get(url)
    Thread.sleep(2500)

    chrome.findElement(By.cssSelector(".css-18t94o4.css-1dbjc4n.r-42olwf.r-sdzlij.r-1phboty.r-rs99b7.r-2yi16.r-1qi8awa.r-1ny4l3l.r-ymttw5.r-o7ynqc.r-6416eg.r-lrvibr")).click()
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