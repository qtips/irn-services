package no.irn.hijri.model

import spray.httpx.marshalling.Marshaller
import spray.http.{HttpEntity, MediaTypes}
import spray.json._

case class HijriDate(val day:Int, val month:Int, val year:Int)

/*case class HijriDate(val date: DateTime)*/
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



