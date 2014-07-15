package no.irn.hijri

import akka.actor.{Props, ActorSystem}
import no.irn.hijri.routes.{HijriServiceActor}
import akka.io.IO
import spray.can.Http
import spray.routing.SimpleRoutingApp

object Boot extends App  {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[HijriServiceActor], "hijri-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, "localhost", port = 8080)

  sys.addShutdownHook(system.shutdown())

}
