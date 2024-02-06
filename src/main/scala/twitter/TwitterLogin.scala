package twitter

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.{By, Keys}

object TwitterLogin {
  def login(twitterId: String, password: String)(implicit chrome: ChromeDriver) = {
    //twitterのログイン画面にアクセス
    chrome.get("https://twitter.com/i/flow/login")

    //twitter idを入力する要素を取得
    val loginElement = chrome.findElement(By.className("r-30o5oe"))
    //twitter idを入力
    loginElement.sendKeys(twitterId)
    //enterで次に行く
    loginElement.sendKeys(Keys.ENTER)
    Thread.sleep(1000)

    //passwordを入力する要素を取得
    val passElement = chrome.findElement(By.name("password"))
    //passwordを入力
    passElement.sendKeys(password)
    //enterで次に行く
    passElement.sendKeys(Keys.ENTER)
    Thread.sleep(2000)
  }
}