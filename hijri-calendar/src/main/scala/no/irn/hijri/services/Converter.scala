package no.irn.hijri.services

import no.irn.hijri.model.{DateRelation, HijriDate}
import akka.actor.Actor
import org.joda.time.DateTime
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import no.irn.hijri.database.IslamicCalendarDBAdapter

class ConverterActor extends Actor {

  //TODO exception handling
  override def receive = {
    case (from:HijriDate,to:HijriDate) => sender() ! Converter.hijriToGregorian(from,to)
    case (from:DateTime,to:DateTime) => sender() ! Converter.gregorianToHijri(from,to)
    case (date:HijriDate) => sender() ! Converter.hijriToGregorian(date)
    case (date:DateTime) => sender() ! Converter.gregorianToHijri(date)
    case _ => sender() ! akka.actor.Status.Failure
  }
}

object Converter {
  def hijriToGregorian(from:HijriDate, to:HijriDate):List[DateRelation] = {
    List(
      DateRelation(HijriDate(1337,1,1),new DateTime(1990,4,1,0,0)),
      DateRelation(HijriDate(1338,1,1),new DateTime(1991,4,1,0,0)),
      DateRelation(HijriDate(1339,1,1),new DateTime(1992,4,1,0,0)))

  }

  def gregorianToHijri(from:DateTime, to:DateTime):List[DateRelation] = {
    // calculate difference between from and to
    // from db fetch all hijri months closest to from (floor) and closest to to (ceil)
    // for each row calculate hijri days until the # difference is achieved.
    null

  }

  def hijriToGregorian(date:HijriDate) = {
    DateRelation(date,new DateTime(2001,1,2,0,0))
  }

  def gregorianToHijri(date:DateTime) = {
    // from db find the first hirji date where date is less than hijri's gregorian date
    val closesHijriDate= IslamicCalendarDBAdapter.getClosestHijriDate(date)

    // add difference in gregorian dates to the hijri date and return.
    closesHijriDate
  }
}
