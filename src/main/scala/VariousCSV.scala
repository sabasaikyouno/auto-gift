import java.io.File

import com.github.tototoshi.csv.{CSVReader, CSVWriter}

import scala.collection.mutable

object VariousCSV {
  def getFollowing = {
    val reader = CSVReader.open(new File("following.csv"))

    reader.readNext.getOrElse(List())
  }

  def followingWrite(list: mutable.HashSet[String]) = {
    val writer = CSVWriter.open(new File("following.csv"))

    writer.writeRow(list.toList)
  }

  def getAccounts = {
    val reader = CSVReader.open(new File("twitter_login.csv"))

    reader.all().flatten.grouped(2).map(l => (l.head, l.last)).toList
  }
}
