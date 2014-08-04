package no.irn.hijri.services

import akka.actor.{ActorSystem, ActorRef, Props, Actor}
import spray.routing.{ExceptionHandler, HttpService}
import org.joda.time.DateTime
import no.irn.hijri.model.DateRelation
import akka.pattern.ask
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import spray.util.LoggingContext
import spray.http.StatusCodes._

class CalendarServiceActor(converterActorRef:ActorRef) extends Actor with CalendarServiceRoute {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context


  // this actor only runs our route, but you could add
  // other things here, like request stream processing,
  // timeout handling or alternative handler registration
  def receive = runRoute(calendarRoute)

  def dateConverterActor = converterActorRef
}

trait CalendarServiceRoute extends HttpService  {

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit lazy val executionContext = actorRefFactory.dispatcher
  implicit lazy val timeout = Timeout(1000, TimeUnit.SECONDS)

  implicit def defaultExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler {
      case e: Exception =>
        requestUri { uri =>
          log.warning("Request to {} could not be handled normally", uri)
          complete(InternalServerError, e.getMessage)
        }
    }


  private lazy val logger = LoggerFactory.getLogger(this.getClass)
  def dateConverterActor: ActorRef
  val gregorianRoute = new HijriServiceRoute(dateConverterActor).gregorianRoute
  val hijriRoute = new GregorianServiceRoute(dateConverterActor).hijriRoute


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
      pathPrefix(gregorianToHijriPath) {
        gregorianRoute
      } ~
      pathPrefix(hijriToGregorianPath) {
        hijriRoute
      }



}
