import java.io.File

import org.openqa.selenium.chrome.ChromeDriver
import java.time.Duration

import TwitterLogin.login
import com.github.tototoshi.csv.CSVReader

object Main {
  def main(args: Array[String]) = {
    implicit val chrome: ChromeDriver = new ChromeDriver()
    chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))

    val twitterLoginIte = CSVReader.open(new File("src\\main\\resources\\twitter_login.csv")).iterator
    val twitterId = twitterLoginIte.next().head
    login(twitterId, twitterLoginIte.next().head)
  }
}
