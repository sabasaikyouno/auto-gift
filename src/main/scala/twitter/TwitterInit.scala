package twitter

import csv.VariousCSV.{getAccounts, writeFollowing}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import twitter.TwitterLogin.login

import java.time.Duration
import scala.collection.mutable

object TwitterInit {
  def initTwitter(followingHashSet: mutable.HashSet[String], myAccountsCookie: mutable.HashSet[String]) = {
    val chrome, chrome2 = createChromeDriver

    scala.sys.addShutdownHook {
      writeFollowing(followingHashSet)
      chrome.quit()
      chrome2.quit()
    }

    getAccounts.foreach { case (id, pass) =>
      login(id, pass)(chrome)
      myAccountsCookie += chrome.manage().getCookieNamed("auth_token").getValue
    }

    getAccounts.foreach { case (id, pass) =>
      login(id, pass)(chrome2)
    }

    (chrome, chrome2)
  }

  private def createChromeDriver = {
    val options = new ChromeOptions()
    options.addArguments("--headless=new")
    options.addArguments("--disable-gpu")
    options.addArguments("--window-size=945,1020")

    val chrome: ChromeDriver = new ChromeDriver(options)
    chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))

    chrome
  }
}
