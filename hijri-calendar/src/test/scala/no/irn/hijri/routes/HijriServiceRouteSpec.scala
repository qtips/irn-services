package no.irn.hijri.routes

import org.scalatest.FlatSpec
import spray.testkit.ScalatestRouteTest

/**
 * Created by qadeer on 12.07.14.
 */
class HijriServiceRouteSpec extends FlatSpec with ScalatestRouteTest with HijriServiceRoutes{

  val actorRefFactory = system

  behavior of "Hijri service routes"


  it should "return todays hijri date for GET request to /today" in {
    Get("/"+HIJRI+"/today") ~> hijriRoute ~> check {
      logger.debug(responseAs[String])
    }
  }

  it should "returns the gregorian calendar for hijri year 1445 when GET request to /1445" in {
    Get("/"+HIJRI+"/"+1445) ~> hijriRoute ~> check {
      logger.debug(responseAs[String])
    }
  }

  it should "returns the gregorian calendar for Ramadan 1445 when GET request to /1445/9" in {
    Get("/"+HIJRI+"/"+1445+"/"+9) ~> hijriRoute ~> check {
      logger.debug(responseAs[String])
    }
  }
  it should "returns the gregorian calendar for 4th Ramadan 1445 when GET request to /1445/9/4" in {
    Get("/"+HIJRI+"/"+1445+"/"+9+"/"+4) ~> hijriRoute ~> check {
      logger.debug(responseAs[String])
    }
  }


}
