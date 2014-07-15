package no.irn.hijri.services

import no.irn.hijri.model.{DateRelation, HijriDate}
import akka.actor.Actor
import org.joda.time.DateTime
import akka.util.Timeout
import java.util.concurrent.TimeUnit

class ConverterActor extends Actor {

  override def receive = {
    case (from:HijriDate,to:HijriDate) => sender() ! Converter.hijriToGregorian(from,to)
    case (from:DateTime,to:DateTime) => sender() ! Converter.gregorianToHijri(from,to)
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
    List(
      DateRelation(HijriDate(1445,1,1),new DateTime(2012,4,1,0,0)),
      DateRelation(HijriDate(1446,1,1),new DateTime(2013,4,1,0,0)),
      DateRelation(HijriDate(1447,1,1),new DateTime(2014,4,1,0,0)))

  }
}
