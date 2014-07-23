package no.irn.hijri.routes

import org.scalatest.FlatSpec
import spray.testkit.ScalatestRouteTest
import akka.actor.{Props}
import no.irn.hijri.services.ConverterActor
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


// TODO
@RunWith(classOf[JUnitRunner])
class HijriServiceRouteSpec extends FlatSpec with ScalatestRouteTest with HijriServiceRoute{

  def actorRefFactory = system
  def dateConverterActor = actorRefFactory.actorOf(Props[ConverterActor], "dateConverter") //TODO move to super trait

  behavior of "Hijri service routes"

              /*
  it should "return todays hijri date for GET request to /today" in {
    Get("/"+hijriToGregorianPath+"/today") ~> hijriRoute ~> check {
      logger.debug(responseAs[String])
    }
  }         */

  it should "returns the gregorian calendar for hijri year 1445 when GET request to /1445" in {
    Get("/"+1445) ~> hijriRoute ~> check {
      logger.debug(responseAs[String])
    }
  }

  it should "returns the gregorian calendar for Ramadan 1445 when GET request to /1445/9" in {
    Get("/"+1445+"/"+9) ~> hijriRoute ~> check {
      logger.debug(responseAs[String])
    }
  }
  it should "returns the gregorian calendar for 4th Ramadan 1445 when GET request to /1445/9/4" in {
    Get("/"+1445+"/"+9+"/"+4) ~> hijriRoute ~> check {
      logger.debug(responseAs[String])
    }
  }

}
