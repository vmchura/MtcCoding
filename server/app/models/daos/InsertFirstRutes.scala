package models.daos

import java.io.PrintWriter

import javax.inject.{ Inject, Singleton }
import models._
import models.mappers.{ CelaRefinedProfile, RouteMobileMapping }
import play.api.Logging
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import shared.RutaSH

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }
@Singleton()
class InsertFirstRutes @Inject() (
  //@NamedDatabase("default")
  protected val dbConfigProvider: DatabaseConfigProvider,
  protected val customColumnTypes: CustomColumnTypes,
  mobileDAO: MobileDAO
)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[CelaRefinedProfile]
  with RouteMobileMapping
  with Logging {

  import profile.api._
  def firstInsert() = {

    val f = mobileDAO.getAllRoutsSH().map { m =>
      PeruRoutes.PeruRoutes.rutasOnMemory = m
      PeruRoutes.PeruRoutes.rutasOnMemory.foreach {
        case (a, b) => println(s"$a + ${b.tagName}")
      }
    }
    f.onComplete(_ => println("END LOAD"))
    /*
    def insertRoute(r: RutaSH): Future[Int] = {
      val ruta = Ruta(-1, r.tagName, r.longitud)
      for {
        rutaID <- mobileDAO.addRuta(ruta)
        insertion <- {
          val coordinates = r.coordinates.map {
            case (lng, lat, prog) =>
              RutaCoordinates(-1, lng, lat, prog.toInt, rutaID)
          }
          mobileDAO.addCoordinates(coordinates)
        }
      } yield {
        insertion
      }
    }
    val allInsertion: Future[List[Int]] = PeruRoutes.PeruRoutes.rutas.foldLeft(Future(List.empty[Int]))((prevFut, r) => {
      for {
        prev <- prevFut
        curr <- insertRoute(r)
      } yield {
        prev :+ curr
      }
    })

    allInsertion.onComplete {
      case Success(_) => println("INSERTIONS MADE!")
      case Failure(error) => println(s"ERROR $error")
    }
*/

  }

  firstInsert()

}
