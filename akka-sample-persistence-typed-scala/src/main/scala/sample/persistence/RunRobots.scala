package sample.persistence

import scala.concurrent.duration._
import scala.util.Success
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import akka.actor.Scheduler

object RunRobots extends App {
  implicit val timeout: Timeout = 5.seconds

  val numberOfRobots = 2

  ActorSystem(RobotManager.init(numberOfRobots), "robot-manager")
}
