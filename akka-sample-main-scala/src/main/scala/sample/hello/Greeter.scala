package sample.hello

import akka.actor.Actor
import org.slf4j.MDC

object Greeter {
  case object Greet
  case object Done
}

class Greeter extends Actor {
  def receive = {
    case Greeter.Greet =>
      println("Hello World! " + MDC.get("key"))
      sender() ! Greeter.Done
  }
}