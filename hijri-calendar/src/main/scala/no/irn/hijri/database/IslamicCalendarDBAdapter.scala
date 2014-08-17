package no.irn.hijri.database

import scala.slick.driver.MySQLDriver.simple._
import no.irn.hijri.database.IslamicCalendarTables.HijriMonths
import org.joda.time.{DateTimeZone, DateTime}
import org.slf4j.LoggerFactory
import no.irn.hijri.model.{HijriDate, DateRelation}
import scala.slick.jdbc.StaticQuery.interpolation

class IslamicCalendarDBAdapter(val dbHost:String, val user:String, val password:String) {


  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  val hijriMonths: TableQuery[HijriMonths] = TableQuery[HijriMonths]
  val db = Database.forURL("jdbc:mysql://"+dbHost+"/islamic_calendar", user = user, password = password, driver = "com.mysql.jdbc.Driver")


  def findFloorFirstInMonth(date: HijriDate) = {
    db.withSession {
      implicit session =>
        val findMonthQuery = HijriMonths.filter(h => h.hijriMonth === date.month && h.hijriYear === date.year)
        logger.debug("executing "+findMonthQuery.selectStatement)
        val hijriMonthRow = findMonthQuery.first
        logger.debug("first result: " + hijriMonthRow)
        rowToDateRelation(hijriMonthRow)

    }
  }

  def getClosestMonthRange(from: HijriDate, to: HijriDate) = {
    db.withSession {
      implicit session =>
        val findMonthRangeQuery = HijriMonths.filter(
          h=>
            h.hijriYear.between(from.year, to.year))

    }
  }

  /**
   * Returns the earliest 1st-in-month Hijri calendar date relation
   * in database for input Gregorian date
   * e.g. input 29th april 2012 will return
   * 1st Jumadi al-thani 1433 <-> 23rd april 2012
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
        logger.debug("first result:" + hijriMonthRow)
        rowToDateRelation(hijriMonthRow)
    }

  }

  /**
   * Finds, from the datbase, all 1st-in-month Hirji dates together with their gregorian mapping
   * for a range defined by from and to. This is essentially the list version of
   * getClosestHijriDate method
   *
   * @see getClosestHijriDate
   *
   * @param from
   * @param to
   * @return list of DateRelation
   */
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

  private def rowToDateRelation(hijriMonth: HijriMonths#TableElementType) = {
    DateRelation(
      HijriDate(hijriMonth.hijriYear, hijriMonth.hijriMonth, 1),
      new DateTime(hijriMonth.irnCalc. getOrElse(new java.sql.Date(0))))     //TODO error handling

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
