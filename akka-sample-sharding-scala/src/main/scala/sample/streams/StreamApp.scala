package sample.streams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.lightbend.cinnamon.scala.future.named.FutureNamed

import scala.concurrent._
import scala.concurrent.duration._

object StreamApp {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
        implicit val ec = system.dispatcher

    val source: Source[Int, NotUsed] = Source(1 to 100)
      .mapAsyncUnordered(10) { i =>
        FutureNamed("future-async-map") {
          doSomeWork()
        }
      }

    // loop to be able to get some metrics
    (1 to 1000) foreach { i =>
      println(s"Iteration: $i")
      val done = source.runForeach(i => doSomeWork() * i)
      Await.ready(done, Duration.Inf)
      Thread.sleep(1000)
    }

    system.terminate()
  }

  def doSomeWork(): Int = {
    new Exception().getStackTrace.length
  }
}
