package no.irn.hijri.model

import org.scalatest.{Matchers, FlatSpec}
import spray.json._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HijriDateSpec extends FlatSpec with Matchers{

  behavior of "HijriDate"
  it should "throw exception when input is invalid" in {

  }

  it should "increase and decrease years correctly" in {

  }

  it should "increase and decrease months correctly" in {

  }

  it should "increase and decrease days correctly" in {

  }


  behavior of "HijriDate Json Protocol"
  it should "format to json" in {
    import HijriDateJsonProtocol._
    val json = HijriDate(1234,4,2).toJson.compactPrint
    json shouldBe "\"1234-04-02\""
  }

}
