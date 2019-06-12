package sample.hello

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Terminated
import org.slf4j.MDC

object Main {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Hello")

    (1 to 100) foreach { i =>
      // set MDC
      MDC.put("key", "value-"+i)

      val a = system.actorOf(Props[HelloWorld], "helloWorld-"+i)
      system.actorOf(Props(classOf[Terminator], a), "terminator-"+i)
    }

    system.terminate()
  }

  class Terminator(ref: ActorRef) extends Actor with ActorLogging {
    context watch ref
    def receive = {
      case Terminated(_) =>
        log.info("{} has terminated", ref.path)
        context.stop(self)
    }
  }

}