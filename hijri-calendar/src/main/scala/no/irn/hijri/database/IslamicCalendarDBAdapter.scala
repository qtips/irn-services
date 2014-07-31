package no.irn.hijri.database

import scala.slick.driver.MySQLDriver.simple._
import no.irn.hijri.database.IslamicCalendarTables.HijriMonths
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import no.irn.hijri.model.{HijriDate, DateRelation}
import scala.slick.jdbc.StaticQuery.interpolation

object IslamicCalendarDBAdapter {


  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  val hijriMonths: TableQuery[HijriMonths] = TableQuery[HijriMonths]
  val db = Database.forURL("jdbc:mysql://localhost/islamic_calendar", user = "root", password = "", driver = "com.mysql.jdbc.Driver")


  /**
   * Returns the closest Hijri calendar date relation in database for input Gregorian date
   * @param date
   * @return no.irn.hijri.model.DateRelation
   */
  def getClosestHijriDate(date: DateTime):DateRelation = {
    val sqlDate = new java.sql.Date(date.getMillis)
    db.withSession {
      implicit session =>
        val closestFloorDateQuery = HijriMonths.closestFloorDate(sqlDate)
        logger.debug("executing: " + closestFloorDateQuery.selectStatement)
        val hijriMonthRow = closestFloorDateQuery.first
        logger.debug("result:" + hijriMonthRow)
        rowToDateRelation(hijriMonthRow)
    }

  }

  def getClosestMonthRange(from: DateTime, to: DateTime) = {
    val fromSql = new java.sql.Date(from.getMillis)
    val toSql = new java.sql.Date(to.getMillis)
    db.withSession {
      implicit session =>
        val closestMonthRangeQuery = closestMonthRange(fromSql, toSql)
        logger.debug("executing: " + closestMonthRangeQuery.getStatement)
        closestMonthRangeQuery.list.map(rowToDateRelation)

    }
  }

  def rowToDateRelation(hijriMonth: HijriMonths#TableElementType) = {
    DateRelation(
      HijriDate(hijriMonth.hijriYear, hijriMonth.hijriMonth, 1),
      new DateTime(hijriMonth.irnCalc getOrElse(new java.sql.Date(0))))     //TODO error handling

  }



  private def closestMonthRange(from: java.sql.Date, to: java.sql.Date)(implicit session: Session) = {
    sql"""
    SELECT *
    FROM hijri_months h1
    WHERE h1.irn_calc >= (SELECT h2.IRN_calc
                          FROM hijri_months h2
                          WHERE h2.irn_calc <= $from
                          ORDER BY h2.irn_calc DESC
                          LIMIT 1)
    AND h1.IRN_calc <= $to;
      """.as[HijriMonths#TableElementType]
  }


  implicit class HijriMonthsExtension(val q: Query[HijriMonths, HijriMonths#TableElementType, Seq]) {
    def closestFloorDate(date: Column[java.sql.Date]) = {
      q.filter(_.irnCalc <= date).sortBy(_.irnCalc.desc).take(1)
    }
  }


}
