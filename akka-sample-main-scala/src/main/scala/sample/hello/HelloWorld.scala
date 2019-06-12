package sample.hello

import akka.actor.{Actor, ActorLogging, Props}

class HelloWorld extends Actor with ActorLogging {

  override def preStart(): Unit = {
    // create the greeter actor
    val greeter = context.actorOf(Props[Greeter], "greeter")
    // tell it to perform the greeting
    greeter ! Greeter.Greet
  }

  def receive = {
    // when the greeter is done, stop this actor and with it the application
    case Greeter.Done =>
      log.info("HelloWorld received Greeter.Done")
      context.stop(self)
  }
}

