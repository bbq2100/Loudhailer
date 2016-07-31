package loudhailer

import java.time.LocalDate

object Model {

  case class Hypothesis(what: String, when: LocalDate = LocalDate.now())

  object Hypothesis {
    import io.circe._
    implicit val decodeHypothesis = Decoder.forProduct1[String, Hypothesis]("_text")(t => Hypothesis(t))
  }

}
