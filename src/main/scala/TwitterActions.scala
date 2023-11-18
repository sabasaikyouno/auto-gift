import org.openqa.selenium.Keys
import org.openqa.selenium.chrome.ChromeDriver

object TwitterActions {
  def likeAndRepost(url: String)(implicit chrome: ChromeDriver): Unit = {
    chrome.get(url)
    Thread.sleep(5000)

    like()
    repost()
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