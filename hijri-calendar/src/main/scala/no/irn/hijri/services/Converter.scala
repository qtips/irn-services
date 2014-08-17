package no.irn.hijri.services

import no.irn.hijri.model.{DateRelation, HijriDate}
import akka.actor.Actor
import org.joda.time.{Days, DateTime}
import no.irn.hijri.database.IslamicCalendarDBAdapter
import org.slf4j.LoggerFactory

class ConverterActor(converter: Converter) extends Actor {


  //TODO exception handling
  override def receive = {

    case (from: HijriDate, to: HijriDate) => sender() ! converter.hijriToGregorian(from, to).get
    case (from: DateTime, to: DateTime) =>
      try {
        val result = converter.gregorianToHijri(from, to).getOrElse(akka.actor.Status.Failure(new Exception("no result")))
        (sender() ! result  )
      } catch {
        case e: Throwable => (sender() ! akka.actor.Status.Failure(e))
      }
    case (date: HijriDate) => sender() ! converter.hijriToGregorian(date)
    case (date: DateTime) => sender() ! converter.gregorianToHijri(date)
    case _ => sender() ! akka.actor.Status.Failure
  }

}

class Converter(val dbHost: String = "localhost", val dbUser: String = "root", val dbPass: String = "") {

  private lazy val logger = LoggerFactory.getLogger(this.getClass)
  private val dbAdapter = new IslamicCalendarDBAdapter(dbHost, dbUser, dbPass)

  def hijriToGregorian(from: HijriDate, to: HijriDate): Option[Seq[DateRelation]] = {
    logger.debug("fetching all months between hijri " + from + " and " + to)
    val monthRange = dbAdapter.findClosestMonthRange(from, to)
    monthRange match {
      case Nil => logger.debug("returning None"); None
      case first :: Nil =>
        logger.debug("returning only first " + first)
        Some(
          generateDateRelations(calculateDateRelation(first, from),
            calculateDateRelation(first, to).gregorian))
      case first :: rest =>
        logger.debug("returning first and rest " + first + "---" + rest)
        Some(
          generateMonthDateRelations(
            calculateDateRelation(first, from) +: rest :+ calculateDateRelation(rest.last, to)))

    }

  }

  def gregorianToHijri(from: DateTime, to: DateTime): Option[Seq[DateRelation]] = {
    // from db fetch all hijri months closest to from (floor) and closest to to (ceil)
    logger.debug("fetching all months between gregorian " + from + " and " + to)
    val monthRange = dbAdapter.getClosestMonthRange(from, to)
    logger.debug("query result: " + monthRange)
    monthRange match {
      case Nil => logger.debug("returning None"); None
      case first :: Nil =>
        logger.debug("returning only first " + first)
        Some(generateDateRelations(calculateDateRelation(first, from), to))
      case first :: rest =>
        logger.debug("returning first and rest " + first + "---" + rest)
        Some(
          generateMonthDateRelations(
            calculateDateRelation(first, from) +: rest :+ calculateDateRelation(rest.last, to)))

    }

  }

  private def generateDateRelations(start: DateRelation, end: DateTime) = {
    for {
      i <- 0 until Days.daysBetween(start.gregorian, end).getDays
    }
    yield {
      DateRelation(
        start.hijri.naivePlusDays(i)
          .getOrElse(throwExceptionForNaiveAddition(i, start.hijri)),
        start.gregorian.plusDays(i)

      )
    }
  }

  private def generateMonthDateRelations(startOfMonthRows: Seq[DateRelation]): Seq[DateRelation] = {
    startOfMonthRows match {
      case first :: Nil => Nil
      case first :: second =>
        generateDateRelations(first, second.head.gregorian) ++ generateMonthDateRelations(second)
    }
  }

  private def throwExceptionForNaiveAddition(days: Int, date: HijriDate) = {
    throw new IllegalArgumentException("Cannot add " + days + " days to hijriDate " + date + ". Incomplete database entry?")
  }

  def hijriToGregorian(hDate: HijriDate) = {
    val closestFirstInMonthDate = dbAdapter.findFloorFirstInMonth(hDate)
    calculateDateRelation(closestFirstInMonthDate, hDate)
  }

  def gregorianToHijri(gDate: DateTime) = {
    val closestFirstInMonthDate = dbAdapter.getClosestHijriDate(gDate)
    calculateDateRelation(closestFirstInMonthDate, gDate)
  }

  private def calculateDateRelation(floorFirstInMonthForDate: DateRelation, forDate: HijriDate) = {
    logger.debug("calculating gregorian date for " + forDate + "with floor=" + floorFirstInMonthForDate)
    DateRelation(
      forDate,
      floorFirstInMonthForDate.gregorian.plusDays(forDate.day - 1)
    )
  }

  private def calculateDateRelation(floorFirstInMonthForDate: DateRelation, forDate: DateTime) = {
    logger.debug("calculating hijri date for " + forDate + " with floor=" + floorFirstInMonthForDate)
    val dayDifference = Days.daysBetween(floorFirstInMonthForDate.gregorian, forDate).getDays
    DateRelation(
      floorFirstInMonthForDate.hijri.naivePlusDays(dayDifference).getOrElse(throwExceptionForNaiveAddition(dayDifference, floorFirstInMonthForDate.hijri)),
      forDate)
  }

}
