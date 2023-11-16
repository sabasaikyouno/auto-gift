import java.io.File

import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import java.time.Duration

import TwitterLogin.login
import com.github.tototoshi.csv.CSVReader

object Main {
  def main(args: Array[String]) = {
    System.setProperty("webdriver.chrome.driver", "chrome\\chromedriver_win32\\chromedriver.exe")
    val options = new ChromeOptions()
    options.addArguments("--user-data-dir=\\chrome")
    options.addArguments("--profile-directory=Profile1")

    implicit val chrome: ChromeDriver = new ChromeDriver(options)
    chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))

    chrome.get("https://twitter.com/home")

    val twitterLoginIte = CSVReader.open(new File("src\\main\\resources\\twitter_login.csv")).iterator
    val twitterId = twitterLoginIte.next().head
    login(twitterId, twitterLoginIte.next().head)
  }
}
