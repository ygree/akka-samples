package sample.persistence

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}

object RobotManager {

  //TODO Use Akka logging. How to do it in Akka Typed?

  sealed trait RobotManageMessage
  case object TryToCreateNewRobot extends RobotManageMessage
  final case class StartNewRobot(robotName: String) extends RobotManageMessage
  final case class RobotTerminated(robotName: String) extends RobotManageMessage

  val CreateNewRobotInterval: FiniteDuration = 10.millis

  def init(targetNumberOfPlayers: Int): Behavior[RobotManageMessage] = {
    Behaviors.withTimers { timers =>
      timers.startPeriodicTimer(TryToCreateNewRobot, TryToCreateNewRobot, CreateNewRobotInterval)
      behavior(targetNumberOfPlayers)
    }
  }

  def behavior(targetNumberOfPlayers: Int, numberOfPlayers: Int = 0): Behavior[RobotManageMessage] = {
    assert(targetNumberOfPlayers > 0)

    Behaviors.setup { ctx =>
      import ctx.executionContext

      Behaviors.receiveMessage {
        case TryToCreateNewRobot =>
          if (targetNumberOfPlayers > numberOfPlayers) {
            val robotName = "robot-" + (Random.nextInt(900000) + 100000)

            println(s"starting new bot $robotName")
            ctx.self ! StartNewRobot(robotName)

            behavior(targetNumberOfPlayers, numberOfPlayers + 1)

          } else {
            Behaviors.same
          }

        case StartNewRobot(robotName) =>
          val robotActor = ctx.spawn(SimpleRobot.init(robotName), robotName)

          ctx.watchWith(robotActor, RobotTerminated(robotName))

          println(
            s"Robot $robotName started. numberOfPlayers = $numberOfPlayers, targetNumberOfPlayers = $targetNumberOfPlayers")

          Behaviors.same

        case RobotTerminated(robotName) =>
          val newNumberOfPlayers = numberOfPlayers - 1

          println(
            s"Robot $robotName terminated. numberOfPlayers = $newNumberOfPlayers, targetNumberOfPlayers = $targetNumberOfPlayers")

          behavior(targetNumberOfPlayers, newNumberOfPlayers)

      }
    }
  }
}

object SimpleRobot {

  implicit val timeout: Timeout = 5.seconds

  sealed trait SimpleRobotMessage

  case object RunPeriodicAction extends SimpleRobotMessage

  case class CartSucceeded() extends SimpleRobotMessage
  case class CartRejected(reason: String) extends SimpleRobotMessage
  case class CartFailed(cause: Throwable) extends SimpleRobotMessage


  val PeriodicActionInterval: FiniteDuration = 100.millis

  def init(robotName: String): Behavior[SimpleRobotMessage] = {
    Behaviors.withTimers { timers =>
      timers.startPeriodicTimer(RunPeriodicAction, RunPeriodicAction, PeriodicActionInterval)
      behavior(robotName)
    }
  }

  def behavior(robotName: String): Behavior[SimpleRobotMessage] = {
    Behaviors.setup { ctx =>
      import ctx.executionContext

      val cart: ActorRef[ShoppingCart.Command[_]] = ctx.spawn(ShoppingCart.behavior(robotName), robotName)

      def updateCart() = {
        val itemName = "Item-" + Random.nextInt(1000)
        val amount = Random.nextInt(100)

        ctx.ask(cart.ref, ShoppingCart.UpdateItem(itemName, amount, _: ActorRef[ShoppingCart.Result])) {
          case Success(ShoppingCart.OK) => CartSucceeded()
          case Success(ShoppingCart.Rejected(msg)) => CartRejected(msg)
          case Failure(e) => CartFailed(e)
        }
      }

      Behaviors.receiveMessage {
        case RunPeriodicAction =>
          updateCart()
          Behaviors.same

        case CartRejected(reason) =>
          println(s"[ $robotName ] Cart rejected with: $reason")
          Behaviors.same

        case CartFailed(err) =>
          println(s"[ $robotName ] Cart failed with: $err")
          Behaviors.same

        case CartSucceeded() =>
          val dice = Random.nextInt(100)

          if (dice < 20) {
            println(s"[ $robotName ] Stopping")
            Behaviors.stopped
          } else {
            // keep shopping
            Behaviors.same
          }
      }
    }
  }
}
