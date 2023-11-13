package sleep

import com.github.nscala_time.time.Imports.DateTime

import scala.concurrent.duration._

class Sleep(req: Int, val sec: Long) {
  private var canReqCount = req
  private var nextResetTime = 0L

  def sleep: Long => Long = defaultSleepTime => {
    val sleepTime = if (canReqCount <= 0) (nextResetTime - DateTime.now().getMillis).max(0).max(defaultSleepTime) else 0

    if (canReqCount == req) {
      nextResetTime = getNextResetTime
      canReqCount -= 1
    } else if (canReqCount <= 0) {
      nextResetTime = getNextResetTime + sleepTime
      canReqCount = req - 1
    } else {
      canReqCount -= 1
    }

    sleepTime
  }

  private def getNextResetTime = DateTime.now().getMillis + sec
}

object Sleep {
  def apply(req: Int, sec: Long): Sleep = new Sleep(req, sec)

  implicit def sleepFImp(sleepCall: Sleep) = sleepCall.sleep
  implicit def sleepImp(reqSec: (Int, FiniteDuration)) = apply(reqSec._1, reqSec._2.toMillis)
}
