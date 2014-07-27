package no.irn.hijri.services

import akka.actor.ActorRef
import akka.pattern.ask
import org.slf4j.LoggerFactory
import no.irn.hijri.model.DateRelation
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import akka.util.Timeout
import spray.routing.Directives

class HijriServiceRoute(val dateConverterActor:ActorRef)(implicit val ec:ExecutionContext, implicit val timeout:Timeout) extends Directives{

  private lazy val logger = LoggerFactory.getLogger(this.getClass)


  // Marshalling support for `application/json`

  import spray.httpx.SprayJsonSupport.sprayJsonMarshaller

  // Json marshalling for DateRelation

  import no.irn.hijri.model.DateRelationJsonProtocol._


  val gregorianRoute =

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
                      (dateConverterActor ?  new DateTime(year, month, day, 0, 0, 0))
                        .mapTo[DateRelation]
                    }
                }
          }
    }


}