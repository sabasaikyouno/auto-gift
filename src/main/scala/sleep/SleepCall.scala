package sleep

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import sleep.SleepActor.Call

import scala.concurrent.duration._
import scala.reflect.ClassTag

class SleepCall(sleepTime: Long => Long, defaultSleepTime: Long, maxSleepTime: Long) {
  private val actorSystem = ActorSystem("SleepActor")
  private val sleepActor = actorSystem.actorOf(Props(new SleepActor))
  implicit private val timeout: Timeout = Timeout(maxSleepTime.seconds)

  def call[A: ClassTag](a: => A) = {
    (sleepActor ? Call(() => a, sleepTime, defaultSleepTime)).mapTo[A]
  }
}

object SleepCall {
  def apply(defaultSleepTime: Long, sleep: Sleep*): SleepCall = {
    val sleepTime = sleep.tail.foldLeft(sleep.head.sleep)(_ andThen _)

    new SleepCall(sleepTime, defaultSleepTime, sleep.maxBy(_.sec).sec)
  }

  def apply(sleep: Sleep*): SleepCall = {
    apply(0, sleep: _*)
  }
}
