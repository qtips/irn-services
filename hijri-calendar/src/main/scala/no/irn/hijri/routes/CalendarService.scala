package no.irn.hijri.routes

import akka.actor.{Props, Actor}
import spray.routing.HttpService
import org.joda.time.DateTime
import no.irn.hijri.model.DateRelation
import akka.pattern.ask
import no.irn.hijri.services.ConverterActor
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory

class CalendarServiceActor extends Actor with CalendarServiceRoute {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context


  // this actor only runs our route, but you could add
  // other things here, like request stream processing,
  // timeout handling or alternative handler registration
  def receive = runRoute(calendarRoute)

}

trait CalendarServiceRoute extends HttpService  {

  private lazy val logger = LoggerFactory.getLogger(this.getClass)
  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  val dateConverterActor = actorRefFactory.actorOf(Props[ConverterActor], "dateConverter")
  val hijriRoute = new HijriServiceRoute(dateConverterActor).hijriRoute
  val gregorianRoute = new GregorianServiceRoute(dateConverterActor).gregorianRoute

  implicit lazy val executionContext = actorRefFactory.dispatcher
  implicit lazy val timeout = Timeout(5, TimeUnit.SECONDS)

  val hijriToGregorianPath = "hijriToGregorian" //TODO get from config
  val gregorianToHijriPath = "gregorianToHijri"

  import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
  import no.irn.hijri.model.DateRelationJsonProtocol._



  val calendarRoute =
    path("today") {
      complete {
        logger.debug("Calling today:")
        val today = DateTime.now()
        (dateConverterActor ? today)
          .mapTo[DateRelation]
        //.recover{case _ =>; "error"}
      }
    } ~
      pathPrefix(hijriToGregorianPath) {
        hijriRoute
      } ~
      pathPrefix(gregorianToHijriPath) {
        gregorianRoute
      }



}
