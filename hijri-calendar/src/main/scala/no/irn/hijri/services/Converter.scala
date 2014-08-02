package no.irn.hijri.services

import no.irn.hijri.model.{DateRelation, HijriDate}
import akka.actor.Actor
import org.joda.time.{Days, DateTime}
import no.irn.hijri.database.IslamicCalendarDBAdapter
import org.slf4j.LoggerFactory

class ConverterActor(converter:Converter) extends Actor {


  //TODO exception handling
  override def receive = {
    case (from: HijriDate, to: HijriDate) => sender() ! converter.hijriToGregorian(from, to)
    case (from: DateTime, to: DateTime) => sender() ! converter.gregorianToHijri(from, to).get
    case (date: HijriDate) => sender() ! converter.hijriToGregorian(date)
    case (date: DateTime) => sender() ! converter.gregorianToHijri(date)
    case _ => sender() ! akka.actor.Status.Failure
  }
}

class Converter(val dbHost:String = "localhost", val dbUser:String ="root", val dbPass:String="") {

  private val dbAdapter = new IslamicCalendarDBAdapter(dbHost,dbUser,dbPass)

  def hijriToGregorian(from: HijriDate, to: HijriDate): List[DateRelation] = {
    List(
      DateRelation(HijriDate(1337, 1, 1), new DateTime(1990, 4, 1, 0, 0)),
      DateRelation(HijriDate(1338, 1, 1), new DateTime(1991, 4, 1, 0, 0)),
      DateRelation(HijriDate(1339, 1, 1), new DateTime(1992, 4, 1, 0, 0)))

  }

  private lazy val logger = LoggerFactory.getLogger(this.getClass)


  private def generateMonthDateRelations(startOfMonthRows: Seq[DateRelation]): Seq[DateRelation] = {
    startOfMonthRows match {
      case first :: Nil => Nil
      case first :: second =>
        generateDateRelations(first, second.head.gregorianDate) ++ generateMonthDateRelations(second)
    }
  }

  private def generateDateRelations(start: DateRelation, end: DateTime) = {
    for {
      i <- 0 until Days.daysBetween(start.gregorianDate, end).getDays
    }
    yield {
      DateRelation(
        start.hijriDate.naivePlusDays(i)
          .getOrElse(throwAssertionErrorForNaiveAddition(i, start.hijriDate)),
        start.gregorianDate.plusDays(i)

      )
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
        Some(generateDateRelations(calculateHijriDate(first, from), to))
      case first :: rest =>
        logger.debug("returning first and rest " + first + "---" + rest)
        Some(
          generateMonthDateRelations(
            calculateHijriDate(first, from) +: rest :+ calculateHijriDate(rest.last, to)))

    }

  }

  private def throwAssertionErrorForNaiveAddition(days: Int, date: HijriDate) = {
    throw new AssertionError("Could not naively add " + days + " days to hijriDate " + date)
  }

  def hijriToGregorian(date: HijriDate) = {
    DateRelation(date, new DateTime(2001, 1, 2, 0, 0))
  }

  def gregorianToHijri(date: DateTime) = {
    val closestFirstInMonthDate = dbAdapter.getClosestHijriDate(date)
    calculateHijriDate(closestFirstInMonthDate, date)
  }

  private def calculateHijriDate(floorFirstInMonthForDate: DateRelation, date: DateTime) = {
    logger.debug("calculating date hijri date for " + date + " with floor=" + floorFirstInMonthForDate)
    val dayDifference = Days.daysBetween(floorFirstInMonthForDate.gregorianDate, date).getDays
    DateRelation(
      floorFirstInMonthForDate.hijriDate.naivePlusDays(dayDifference).getOrElse(throwAssertionErrorForNaiveAddition(dayDifference, floorFirstInMonthForDate.hijriDate)),
      date)
  }
}
