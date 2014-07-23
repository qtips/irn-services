package no.irn.hijri.model

import org.scalatest.{Matchers, FlatSpec}
import org.joda.time.DateTime
import spray.json._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class DateRelationAndDateTimeJsonProtocolSpec extends FlatSpec with Matchers {

  behavior of "DateTime"
  it should "format to json" in {
    import DateTimeJsonProtocol._
    val json = new DateTime(2014,3,1,0,0).toJson.compactPrint
    json shouldBe "\"2014-03-01\""
  }


  behavior of "DateRelation"
  it should "format to json" in {
    import DateRelationJsonProtocol._
    val hijri = HijriDate(4321,9,8)
    val gregorian = new DateTime(2011,2,3,1,2)
    val json = DateRelation(hijri,gregorian).toJson.compactPrint
    json shouldBe "{\"hijriDate\":\"4321-09-08\",\"gregorianDate\":\"2011-02-03\"}"

  }


}
