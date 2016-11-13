package github.qabbasi.loudhailer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import cats.data.Xor
import com.typesafe.config.{Config, ConfigFactory}
import de.heikoseeberger.akkahttpcirce.CirceSupport
import Model.Hypothesis
import SoundRecorder.Sample

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}
import io.circe.syntax._

import scala.language.postfixOps

object LoudHailer extends App with CirceSupport {
  val config: Config = ConfigFactory.load()
  val witToken: String = config.getString("wit.token")
  val witUrl: String = config.getString("wit.url")
  val fireBaseToken: String = config.getString("firebase.token")
  val fireBaseUrl: String = config.getString("firebase.url")

  implicit val system = ActorSystem("LoudhailerSystem")
  implicit val materializer = ActorMaterializer()

  val blackList = List("help")

  def sample: Unit => Sample = _ => SoundRecorder.sample

  def request: Array[Byte] => Future[HttpResponse] = data =>
    Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = witUrl,
      headers = List(headers.RawHeader("Authorization", s"Bearer $witToken")),
      entity = HttpEntity(contentType = `audio/wav`, data)))

  def analyse: Sample => Future[Hypothesis] = {
    case Xor.Left(e) => Future.failed(e)
    case Xor.Right(data) =>
      for {
        response <- request(data)
        hypothesis <- Unmarshal(response.entity).to[Hypothesis]
      } yield hypothesis
  }

  def act: (Future[Hypothesis]) => Unit = f => {
    f.onComplete {
      case Success(h) => if (blackList.contains(h.what)) broadcastEvent()
      case Failure(e) => e.printStackTrace()
    }
  }

  def broadcastEvent() = {
    val body = Map(
      "to" -> "/topics/alert".asJson,
      "data" -> Map(
        "message" -> "The alarm was triggered.".asJson
      ).asJson
    ).asJson

    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = fireBaseUrl,
        headers = List(headers.RawHeader("Authorization", s"key=$fireBaseToken")),
        entity = HttpEntity(contentType = `application/json`, body.noSpaces)))
  }

  showBanner()

  Source.tick(0 second, 5 seconds, ())
        .map(sample)
        .map(analyse)
        .runForeach(act)

  println("Press <enter> to exit...")

  System.in.read()
  materializer.shutdown()
  system.terminate()
}