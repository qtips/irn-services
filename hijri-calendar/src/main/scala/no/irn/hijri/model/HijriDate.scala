package no.irn.hijri.model

import com.github.nscala_time.time.Imports._
import spray.httpx.marshalling.Marshaller
import spray.http.{HttpEntity, MediaTypes}

/**
 * Created by qadeer on 21.05.14.
 */
case class HijriDate(val date: DateTime)

object HijriDate {
  def apply(day:Int, month:Int, year:Int) = new HijriDate(new DateTime(year, month, day))
  implicit val HijriDateMarshaller =
    Marshaller.of[HijriDate](MediaTypes.`text/html`) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType,value.date.toString()))

    }
}
