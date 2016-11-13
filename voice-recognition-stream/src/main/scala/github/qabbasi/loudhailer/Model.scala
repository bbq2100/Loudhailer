package github.qabbasi.loudhailer

object Model {

  case class Hypothesis(what: String)

  object Hypothesis {
    import io.circe._
    implicit val decodeHypothesis = Decoder.forProduct1[String, Hypothesis]("_text")(Hypothesis.apply)
  }

}
