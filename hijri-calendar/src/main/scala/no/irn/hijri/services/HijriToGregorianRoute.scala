package no.irn.hijri.services

import akka.pattern.ask
import org.slf4j.LoggerFactory
import akka.actor.ActorRef
import no.irn.hijri.model.{HijriDate, DateRelation}
import scala.concurrent.ExecutionContext
import akka.util.Timeout
import spray.routing.Directives

class HijriToGregorianRoute(val dateConverterActor:ActorRef)(implicit val ec:ExecutionContext, implicit val timeout:Timeout) extends Directives {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  // Marshalling support for `application/json`

  import spray.httpx.SprayJsonSupport.sprayJsonMarshaller

  // Json marshalling for DateRelation

  import no.irn.hijri.model.DateRelationJsonProtocol._

  val hijriRoute =

    pathPrefix(IntNumber) {
      year =>
        pathEnd {
          complete {
            logger.debug("Calling /" + year)
            val requestYear = HijriDate(year, 1, 1)
            (dateConverterActor ?(requestYear, requestYear.plusYears(1)))
              .mapTo[Seq[DateRelation]]
          }
        } ~
          pathPrefix(IntNumber) {
            month =>
              pathEnd {
                complete {
                  logger.debug("Calling /" + year + "/" + month)
                  val requestYearMonth = HijriDate(year, month, 1)
                  (dateConverterActor ?(requestYearMonth, requestYearMonth.plusMonths(1)))
                    .mapTo[Seq[DateRelation]]
                }
              } ~
                path(IntNumber) {
                  day =>
                    complete {
                      logger.debug("Calling /" + year + "/" + month + "/" + day)
                      (dateConverterActor ? HijriDate(year, month, day))
                        .mapTo[DateRelation]
                    }
                }
          }
    }

}
