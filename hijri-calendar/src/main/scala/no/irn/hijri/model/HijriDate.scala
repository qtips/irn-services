package no.irn.hijri.model

import spray.json._
import no.irn.hijri.model.HijriCalendarSource.HijriCalendarSource

case class HijriDate(val year: Int, val month: Int, val day: Int, val leapYear: Boolean = false) {

  implicit def intToIntWithRangeSupport(source: Int) = new {
    def isBetween(lower: Int, upper: Int) = source >= lower && source <= upper
  }

  /* constructor code */
  if (!(year > 0 && month.isBetween(1, 12) && day.isBetween(1, 30)))
    throw new IllegalArgumentException("HijriDate is invalid. Rules: year>0, 12>=month>=1 and 30>=day>=1, but found "+year+"/"+month+"/"+day)


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

  def plusYears(years: Int) = {
    HijriDate(year + years, month, day)
  }

  /*
   * Creates a new HijriDate by adding days to this HijriDate naively, i.e.
   * assumes that all months are 30 days and all years are 354 years. This
   * method should only be used for adding days so that the new date stays
   * in the same month
   *
   * This method does not take into account if the resulting date
   * is valid in an Islamic calendar <b>IF</b> the resulting date has
   * day = 29 or 30.
   *
   * To verify if such date is valid
   * use {@link dateExistsInHijriSource} (because a database call is then issued).
   *
   *
   *
   */
  /*
  def naivePlusDays(days: Int) {
    val daysInYear = 354
    val daysInMonth = 30
    HijriDate(
      year + (days / daysInYear) ,
      month + ((days % daysInYear ) / daysInMonth),
      day + (days % daysInYear) % daysInMonth)
  }
    */

  /**
   * Creates new HijriDate by adding days to current HijriDate
   * if the sum of days is less than 31 and larger than -1.
   *
   * This method does not take into account if the resulting date
   * is valid in an Islamic calendar <b>IF</b> the resulting date has
   * day = 29 or 30.
   *
   * To verify if such date is valid
   * use {@link dateExistsInHijriSource} (because a database call is then issued).

   *
   * @param days
   * @return Some(HijriDate) if less than 31 else None
   */
  def naivePlusDays(days: Int) = {
    if (day + days <=30 && day + days >= 0) Some(HijriDate(year,month,day+days)) else None
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