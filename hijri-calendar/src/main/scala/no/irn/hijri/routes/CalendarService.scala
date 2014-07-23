package no.irn.hijri.routes

import akka.actor.{Props, Actor}
import spray.routing.HttpService
import org.joda.time.DateTime
import no.irn.hijri.model.DateRelation
import akka.pattern.ask
import no.irn.hijri.services.ConverterActor

class CalendarServiceActor extends Actor with CalendarServiceRoute {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing,
  // timeout handling or alternative handler registration
  def receive = runRoute(calendarRoute)

}

trait CalendarServiceRoute extends HttpService with HijriServiceRoute {

  val dateConverterActor = actorRefFactory.actorOf(Props[ConverterActor], "dateConverter")

  //TODO move to super trait
  val hijriToGregorianPath = "hijriToGregorian" //TODO get from config

  import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
  import no.irn.hijri.model.DateRelationJsonProtocol._

  val calendarRoute =
    path("today") {
      complete {
        logger.debug("Calling today:")
        val today = DateTime.now()
        (dateConverterActor ?(today, today.plusDays(1)))
          .mapTo[List[DateRelation]]
          .map(result => result.head) // "return" the first element in result
        //.recover{case _ =>; "error"}
      }
    } ~
      pathPrefix(hijriToGregorianPath ) {
        hijriRoute
      }


  /* ~
  path("greogiranToHijri") {

  } ~
 */

}
