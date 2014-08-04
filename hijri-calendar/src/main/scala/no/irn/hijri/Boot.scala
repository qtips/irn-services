package no.irn.hijri

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import spray.can.Http
import no.irn.hijri.services.{ConverterActor, Converter, CalendarServiceActor}

object Boot extends App {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")


  // setup converter with database with commandline arguments
  var converter = system.actorOf(Props(classOf[ConverterActor],
    new Converter(
      dbHost = Option(args(2)).getOrElse("localhost"),
      dbUser = Option(args(3)).getOrElse("root"),
      dbPass = Option(args(4)).getOrElse(""))),
    "dateConverter")

  // create and start our service actor
  //val service = system.actorOf(Props(classOf[CalendarServiceActor],converter), "hijri-service")
  val service = system.actorOf(Props(classOf[CalendarServiceActor], converter), "hijri-service")


  val interface = Option(args(0)).getOrElse("localhost")
  val port = Option(args(1)).getOrElse("8080").toInt

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = interface, port = port)

  sys.addShutdownHook(system.shutdown())

}
