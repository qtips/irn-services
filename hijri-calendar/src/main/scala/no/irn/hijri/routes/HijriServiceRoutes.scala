package no.irn.hijri.routes

import akka.actor.{Actor, ActorSystem}
import spray.routing.{HttpService, SimpleRoutingApp}
import org.slf4j.LoggerFactory
import no.irn.hijri.model.HijriDate
import org.joda.time.DateTime
import akka.actor.Actor.Receive

/**
 * Created by qadeer on 19.05.14.
 */

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class HijriServiceActor extends Actor with HijriServiceRoutes {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing,
  // timeout handling or alternative handler registration
  def receive = runRoute(hijriRoute)

}

trait HijriServiceRoutes extends HttpService {

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  val HIJRI = "hijri"
  lazy val logger = LoggerFactory.getLogger(this.getClass)

  import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
  import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
  import no.irn.hijri.model.DateTimeJsonProtocol._

  val hijriRoute =
    pathPrefix(HIJRI) {
      path("today") {
        complete {
          logger.debug("Calling today")
          HijriDate(DateTime.now)

        }
      } ~
        pathPrefix(IntNumber) {
          year =>
            pathEnd {
              complete {
                logger.debug("Calling /" + year)
                "year:" + year
              }
            } ~
              pathPrefix(IntNumber) {
                month =>
                  pathEnd {
                    complete {
                      logger.debug("Calling /" + year + "/" + month)
                      "year:" + year + " and month " + month
                    }
                  } ~
                    path(IntNumber) {
                      day =>
                        complete {
                          logger.debug("Calling /" + year + "/" + month + "/" + day)
                          "year:" + year + " and month " + month + " and day "+day
                        }
                    }
              }
        }
    }
}