package no.irn.hijri.model

import org.scalatest.{Matchers, FlatSpec}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HijriDateDatabaseValidationSpec extends FlatSpec with Matchers
{
  behavior of "HijriDate.validateFromHijriSource"

  it should "invalidate a date if it is not consistent with source" in {

  }

}
