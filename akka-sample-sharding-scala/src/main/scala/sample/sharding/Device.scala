package sample.sharding

import akka.actor._
import akka.pattern.pipe
import scala.concurrent.Future


/**
 * This is just an example: cluster sharding would be overkill for just keeping a small amount of data,
 * but becomes useful when you have a collection of 'heavy' actors (in terms of processing or state)
 * so you need to distribute them across several nodes.
 */
object Device {
  case class RecordTemperature(deviceId: Int, temperature: Double)
  case class GetTemperature(deviceId: Int)
  case class Temperature(deviceId: Int, temperature: Double)
}
class Device extends Actor with ActorLogging {
  import Device._

  override def receive = counting(Nil)

  // ---> use custom dispatcher to see if
  val redisEC = context.system.dispatchers.lookup("redis-dispatcher")

  def counting(values: List[Double]): Receive = {
    case RecordTemperature(id, temp) =>
      val temperatures = temp :: values
      log.info(s"Recording temperature $temp for device $id, average is ${temperatures.sum / temperatures.size} after ${temperatures.size} readings");
      context.become(counting(temperatures))

    case GetTemperature(id) =>
      // running future on redisEC
      val calcOnRedisEC = Future {
        Thread.sleep(50)
        Temperature(id, values.head)
      }(redisEC)
      // pipe the result back to the sender
      pipe(calcOnRedisEC)(context.dispatcher) to sender
  }
}
