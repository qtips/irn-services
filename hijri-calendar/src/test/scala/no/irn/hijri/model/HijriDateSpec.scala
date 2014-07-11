package no.irn.hijri.model

import org.scalatest.{Matchers, FlatSpec}
import org.joda.time.DateTime
import spray.json._
import DateTimeJsonProtocol._

/**
 * Created by qadeer on 22.05.14.
 */
class HijriDateSpec extends FlatSpec with Matchers {

  behavior of "DateTime"
  it should "format to json" in {

    val json = DateTime.now.toJson
    println(json.toString)
  }

  behavior of "HijriDate"
  it should "format to json" in {
    val json = HijriDate(DateTime.now).toJson
    println("hijridate"+json.toString())
  }


}
