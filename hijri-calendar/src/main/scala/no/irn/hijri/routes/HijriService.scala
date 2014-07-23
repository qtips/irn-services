package no.irn.hijri.routes

import akka.actor.{ActorRef}
import akka.pattern.ask
import spray.routing.{HttpService}
import org.slf4j.LoggerFactory
import no.irn.hijri.model.{DateRelation}
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import org.joda.time.DateTime

trait HijriServiceRoute extends HttpService {
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  lazy val logger = LoggerFactory.getLogger(this.getClass)


  // Marshalling support for `application/json`

  import spray.httpx.SprayJsonSupport.sprayJsonMarshaller

  // Json marshalling for DateRelation

  import no.irn.hijri.model.DateRelationJsonProtocol._

  implicit def dateConverterActor: ActorRef //actorRefFactory.actorOf(Props[ConverterActor], "dateConverter")


  val hijriRoute =

    pathPrefix(IntNumber) {
      year =>
        pathEnd {
          complete {
            logger.debug("Calling /" + year)
            val requestYear = new DateTime(year, 1, 1, 0, 0, 0)
            (dateConverterActor ?(requestYear, requestYear.plusYears(1)))
              .mapTo[List[DateRelation]]
          }
        } ~
          pathPrefix(IntNumber) {
            month =>
              pathEnd {
                complete {
                  logger.debug("Calling /" + year + "/" + month)
                  val requestYearMonth = new DateTime(year, month, 1, 0, 0, 0)
                  (dateConverterActor ?(requestYearMonth, requestYearMonth.plusMonths(1)))
                    .mapTo[List[DateRelation]]
                }
              } ~
                path(IntNumber) {
                  day =>
                    complete {
                      logger.debug("Calling /" + year + "/" + month + "/" + day)
                      val requestDay = new DateTime(year, month, day, 0, 0, 0)
                      (dateConverterActor ?(requestDay, requestDay.plusDays(1)))
                        .mapTo[List[DateRelation]]
                    }
                }
          }
    }


}