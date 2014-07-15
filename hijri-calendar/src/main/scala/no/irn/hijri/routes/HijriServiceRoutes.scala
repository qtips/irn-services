package no.irn.hijri.routes

import akka.actor.{Props, Actor, ActorSystem}
import akka.pattern.ask
import spray.routing.{HttpService, SimpleRoutingApp}
import org.slf4j.LoggerFactory
import no.irn.hijri.model.{DateRelation, HijriDate}
import no.irn.hijri.services.{ConverterActor, Converter}
import akka.util.Timeout
import java.util.concurrent.TimeUnit

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

  val dateConverter = context.actorOf(Props[ConverterActor], "dateConverter")

}

trait HijriServiceRoutes extends HttpService {
  implicit val timeout = Timeout(5,TimeUnit.SECONDS)

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
          logger.debug("Calling today:")
          (actorRefFactory.actorOf(Props[ConverterActor])
            ? (HijriDate(1234, 12, 3), HijriDate(3214, 23, 1)))
            .mapTo[List[DateRelation]]
            .map(result => s"I got a response ${result}").recover{case _ =>; "error"}
        }
      } ~
        pathPrefix(IntNumber) {
          year =>
            pathEnd {
              complete {
                logger.debug("Calling /" + year)
                ""

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