package no.irn.hijri.model

import spray.json._
import no.irn.hijri.model.HijriCalendarSource.HijriCalendarSource

case class HijriDate(val year: Int, val month: Int, val day: Int) {

  implicit def intToIntWithRangeSupport(source: Int) = new {
    def isBetween(lower: Int, upper: Int) = source >= lower && source <= upper
  }

  /* constructor code */
  if (!(year > 0 && month.isBetween(1, 12) && day.isBetween(1, 30)))
    throw new IllegalArgumentException("HijriDate is invalid. Rules: year>0, 12>=month>=1 and 30>=day>=1 ")


  def plusMonths(months: Int) = {
    val monthSum = month + months
    if (monthSum < 12)
      HijriDate(year, monthSum, day)
    else {
      HijriDate(
        year + (monthSum / 12),
        month + (monthSum % 12),
        day)
    }
  }

  /**
   * Do not know how many days there are in a month - must check with database
   * @param days
   */
  def plusDays(days: Int) = {

  }

  def plusYears(years: Int) = {
    HijriDate(year + years, month, day)
  }

  def dateExitsInHijriSource(source: HijriCalendarSource = HijriCalendarSource.HijriavtaleDecided) = {
    //TODO check in database
  }


}

object HijriCalendarSource extends Enumeration {
  type HijriCalendarSource = Value
  val HijriavtaleCalculated = Value("Hijriavtale_calculated")
  val HijriavtaleDecided = Value("Hijriavtale_decided")
  val TurkeyCalculated = Value("Turkey_calculated")
  val UmmulQura = Value("UmmulQura")
  val SaudiAnnounced = Value("Saudi_announced")
}

object HijriDateJsonProtocol extends DefaultJsonProtocol {

  implicit object HijriDateJsonFormat extends RootJsonFormat[HijriDate] {
    def write(date: HijriDate) = {
      JsString(date.year + "-"
        + (if (date.month < 10) "0" + date.month else date.month) + "-"
        + (if (date.day < 10) "0" + date.day else date.day))
    }

    def read(value: JsValue) = value match {
      case JsString(date) =>
        val year :: month :: day :: _ = date.split("-").toList
        HijriDate(year.toInt, month.toInt, day.toInt)
      case _ => throw new DeserializationException("expected a string of format year-month-day")

    }

  }

}