package sample.sharding

import akka.actor._
import akka.pattern.pipe
import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.stream.ActorMaterializer
import com.lightbend.cinnamon.scala.future.named.FutureNamed
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

  implicit val materializer = ActorMaterializer()
  implicit val ec = context.system.dispatcher

  // ---> use custom dispatcher to see if
  val redisEC = context.system.dispatchers.lookup("redis-dispatcher")


  def counting(values: List[Double]): Receive = {
    case RecordTemperature(id, temp) =>
      val temperatures = temp :: values
      log.info(s"Recording temperature $temp for device $id, average is ${temperatures.sum / temperatures.size} after ${temperatures.size} readings");
      context.become(counting(temperatures))

    case GetTemperature(id) =>
      val source: Source[Int, NotUsed] = Source(1 to 100)
        .mapAsyncUnordered(10) { i =>
          FutureNamed("future-async-map") {
            doSomeWork()
          } (redisEC) // <-- with our without custom dispatcher the future metrics are produced
        }

      val done = source.runForeach(i => doSomeWork() * i)

      val result = done.flatMap { _ =>

        // running future on redisEC
        val calcOnRedisEC = FutureNamed.withName("redis-call")(
          Future {
            Thread.sleep(50)
            Temperature(id, values.head)
          }(redisEC))
        calcOnRedisEC
      }
      // pipe the result back to the sender
      pipe(result)(context.dispatcher) to sender
  }

  def doSomeWork(): Int = {
    new Exception().getStackTrace.length
  }
}
