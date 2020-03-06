package models.daos

import java.io.PrintWriter

import javax.inject.{ Inject, Singleton }
import models.mappers.{ CelaRefinedProfile, RouteMobileMapping }
import play.api.Logging
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }

@Singleton()
class CreateDDL @Inject() (
  //@NamedDatabase("default")
  protected val dbConfigProvider: DatabaseConfigProvider,
  protected val customColumnTypes: CustomColumnTypes
)
  extends HasDatabaseConfigProvider[CelaRefinedProfile]
  with RouteMobileMapping
  with Logging {

  import profile.api._
  import profile.mapping._
  def createDDLScript() = {

    logger.info("Starting to create ddl.sql")

    val allSchemas = Seq(
      limiteVelocidadTQ,
      rutaTQ,
      rutaCoordiantesTQ,
      pasajeroTQ,
      travelTQ,
      dataTravelTQ
    ).map(_.schema).reduceLeft(_ ++ _)

    val writer = new PrintWriter("target/mobile.sql")
    writer.write("# --- !Ups\n\n")
    allSchemas.createStatements.foreach { s => writer.write(s + ";\n") }

    writer.write("\n\n# --- !Downs\n\n")
    allSchemas.dropStatements.foreach { s => writer.write(s + ";\n") }

    writer.close()

    logger.info("Created ddl.sql")

  }

  createDDLScript()

}
