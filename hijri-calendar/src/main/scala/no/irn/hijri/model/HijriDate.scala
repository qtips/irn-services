package no.irn.hijri.model

import com.github.nscala_time.time.Imports._
import spray.httpx.marshalling.Marshaller
import spray.http.{HttpEntity, MediaTypes}
import spray.json._

/**
 * Created by qadeer on 21.05.14.
 */
case class HijriDate(val date: DateTime)
 /*
object HijriDate {
  //def apply(day:Int, month:Int, year:Int) = new HijriDate(new DateTime(year, month, day))
  implicit val HijriDateMarshaller =
    Marshaller.of[HijriDate](MediaTypes.`text/html`) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType,value.date.toString()))

    }
}     */
        /*
object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val HijriDateFormat = jsonFormat1(HijriDate.apply)
}     */

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
  implicit val HijriDateFormat = jsonFormat1(HijriDate.apply)
}


