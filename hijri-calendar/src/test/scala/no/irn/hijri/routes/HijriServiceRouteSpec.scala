package no.irn.hijri.routes

import org.scalatest.FlatSpec
import spray.testkit.ScalatestRouteTest
import akka.actor.{Props}
import no.irn.hijri.services.{HijriServiceRoute, ConverterActor}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.slf4j.LoggerFactory
import akka.util.Timeout
import java.util.concurrent.TimeUnit


// TODO
@RunWith(classOf[JUnitRunner])
class HijriServiceRouteSpec extends FlatSpec with ScalatestRouteTest {


  lazy val logger = LoggerFactory.getLogger(this.getClass())
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)
  val dateConverterActor = system.actorOf(Props[ConverterActor], "dateConverter") //TODO move to super trait
  val hijriRoute =  new HijriServiceRoute(dateConverterActor).gregorianRoute

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
