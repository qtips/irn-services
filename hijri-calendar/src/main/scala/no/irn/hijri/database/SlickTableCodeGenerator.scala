package no.irn.hijri.database

import scala.slick.model.codegen.SourceCodeGenerator

object SlickTableCodeGenerator {
  def main(args: Array[String]) {

    // Must add "org.scala-lang:scala-reflect" in dependency in order for this to work!

    SourceCodeGenerator.main(Array(
      "scala.slick.driver.MySQLDriver",
      "com.mysql.jdbc.Driver",
      "jdbc:mysql://localhost/islamic_calendar",
       "IRN/hijri-calendar/src/main/scala/no/irn/hijri/database",
       "no.irn.hijri.database",
       "root",
        ""
    ))
  }
}
