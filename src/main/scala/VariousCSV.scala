import java.io.File

import com.github.tototoshi.csv.{CSVReader, CSVWriter}

object VariousCSV {
  def getFollowing = {
    val reader = CSVReader.open(new File("src\\main\\resources\\following.csv"))

    reader.readNext.getOrElse(List())
  }

  def getAccounts = {
    val reader = CSVReader.open(new File("src\\main\\resources\\twitter_login.csv"))

    reader.all().flatten.grouped(2).map(l => (l.head, l.last)).toList
  }
}
