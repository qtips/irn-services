package no.irn.hijri.database

import scala.slick.driver.MySQLDriver.simple._
import no.irn.hijri.database.IslamicCalendarTables.HijriMonths
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import no.irn.hijri.model.{HijriDate, DateRelation}

object IslamicCalendarDBAdapter {

  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  val hijriMonths: TableQuery[HijriMonths] = TableQuery[HijriMonths]
  val db = Database.forURL("jdbc:mysql://localhost/islamic_calendar", user ="root" , password="", driver = "com.mysql.jdbc.Driver")

  def getClosestHijriDate(date: DateTime) = {
    val sqlDate = new java.sql.Date(date.getMillis)
    db.withSession {
      implicit session =>
        val filterQuery = hijriMonths
          .filter(_.irnCalc <= sqlDate)
          .sortBy(_.irnCalc.desc)
          .take(1)
        logger.debug("executing: " + filterQuery.selectStatement)
        val hijriMonth = filterQuery.first
        logger.debug("result:" + hijriMonth)
        DateRelation(
          HijriDate(hijriMonth.hijriYear, hijriMonth.hijriMonth, 1),
          new DateTime(hijriMonth.irnCalc getOrElse(new java.sql.Date(0))))     //TODO error handling
    }

  }

}
