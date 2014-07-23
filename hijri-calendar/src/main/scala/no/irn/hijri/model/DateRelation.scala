package no.irn.hijri.model

import org.joda.time.DateTime
import spray.json._
import org.joda.time.format.DateTimeFormat

case class DateRelation(hijriDate: HijriDate, gregorianDate: DateTime)


object DateTimeJsonProtocol extends DefaultJsonProtocol {

  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {
    val formatter = DateTimeFormat.forPattern("YYYY-MM-dd")

    def write(date: DateTime) =
      JsString(date.toString(formatter))

    def read(value: JsValue) = {
      value.asJsObject.getFields("date") match {
        case Seq(JsString(date)) => formatter.parseDateTime(date)
        case _ => throw new DeserializationException("Joda DateTime expected")
      }
    }
  }

}

object DateRelationJsonProtocol extends DefaultJsonProtocol {

  import DateTimeJsonProtocol._
  import HijriDateJsonProtocol._

  implicit val dateRelationFormat = jsonFormat2(DateRelation)
}



