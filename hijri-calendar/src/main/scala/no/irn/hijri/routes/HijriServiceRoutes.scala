package no.irn.hijri.routes

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import org.slf4j.LoggerFactory
import no.irn.hijri.model.HijriDate
import org.joda.time.DateTime

/**
 * Created by qadeer on 19.05.14.
 */
object HijriServiceRoutes extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("my-system")
  val HIJRI = "hijri"
  lazy val logger = LoggerFactory.getLogger(this.getClass)


  startServer(interface = "localhost", port = 8080) {
    path(HIJRI / "today") {
      get {
        complete {
          logger.debug("Calling today")
          HijriDate(DateTime.now)

        }
      }
    }
  }
}