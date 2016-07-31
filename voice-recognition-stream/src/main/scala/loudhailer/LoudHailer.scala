package loudhailer

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
import loudhailer.Model.Hypothesis
import loudhailer.SoundRecorder.{Data, Sample}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

/*
TODO: In-memory data (replace file)
 */

object LoudHailer extends App with CirceSupport {
  val config: Config = ConfigFactory.load()
  val witToken: String = config.getString("wit.token")
  val witUrl: String = config.getString("wit.url")

  implicit val system = ActorSystem("LoudhailerSystem")
  implicit val materializer = ActorMaterializer()

  val blackList = List("help")

  def request: Data => Future[HttpResponse] = data =>
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

  def act: (Future[Hypothesis]) => Unit = h => {
    h.onComplete {
      case Success(a) =>
        if (blackList.contains(a.what)) println("help")
        else println("all good")
      case Failure(e) => e.printStackTrace()
    }
  }

  showBanner()

  val sourceTick = Source.tick(0 second, 5 seconds, ())

  def sample: Unit => Sample = _ => SoundRecorder.sample

  //  format:off

  sourceTick.map(sample)
            .map(analyse)
            .runForeach(act)

  //  format:on

  println("Press <enter> to exit...")

  System.in.read()
  materializer.shutdown()
  system.terminate()
}