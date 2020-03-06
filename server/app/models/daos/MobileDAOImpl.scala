package models.daos
import javax.inject.{ Inject, Singleton }
import models.{ DataTravel, LimiteVelocidad, Pasajero, Ruta, RutaCoordinates, Travel }
import models.mappers.{ CelaRefinedProfile, RouteMobileMapping }

import scala.concurrent.{ ExecutionContext, Future }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
@Singleton()
class MobileDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider,
  protected val customColumnTypes: CustomColumnTypes)(implicit executionContext: ExecutionContext) extends MobileDAO with RouteMobileMapping with HasDatabaseConfigProvider[CelaRefinedProfile] {

  import profile.api._
  import customColumnTypes.jodaDateTimeType

  val rutaWithID = (rutaTQ returning rutaTQ.map(_.idRuta) into ((ruta, newID) => ruta.copy(idRuta = newID)))
  override def addRuta(ruta: Ruta): Future[Int] = db.run(rutaWithID += ruta).map(_.idRuta)

  override def addCoordinates(rutaCoordinates: Seq[RutaCoordinates]): Future[Int] = db.run(rutaCoordiantesTQ ++= rutaCoordinates).map(x => x.getOrElse(0))

  override def getRuta(idRuta: Int): Future[Option[Ruta]] = db.run(rutaTQ.filter(_.idRuta === idRuta).result.headOption)

  override def getCoordinates(idRuta: Int): Future[Seq[RutaCoordinates]] = db.run(rutaCoordiantesTQ.filter(_.idRuta === idRuta).sortBy(_.prog).result)

  val pasajeroWithID = (pasajeroTQ returning pasajeroTQ.map(_.idPasajero) into ((pasajero, newID) => pasajero.copy(idPasajero = newID)))

  override def addPasajero(pasajero: Pasajero): Future[Int] = db.run(pasajeroWithID += pasajero).map(_.idPasajero)

  override def getPasajero(idPasajero: Int): Future[Option[Pasajero]] = db.run(pasajeroTQ.filter(_.idPasajero === idPasajero).result.headOption)

  val travelWithID = (travelTQ returning travelTQ.map(_.idTravel) into ((travel, newID) => travel.copy(idTravel = newID)))

  override def addTravel(travel: Travel): Future[Int] = db.run(travelWithID += travel).map(_.idTravel)

  override def getTravel(idTravel: Int): Future[Option[Travel]] = db.run(travelTQ.filter(_.idTravel === idTravel).result.headOption)

  override def addDataTravel(dataTravel: DataTravel): Future[Int] = {
    if (dataTravel.velocidad >= 0) {
      db.run(dataTravelTQ += dataTravel)
    } else {
      db.run(travelTQ.filter(_.idTravel === dataTravel.idTravel).map(_.isLive).update(false)).map(_ => -1)
    }
  }

  override def getDataFromTravel(idTravel: Int): Future[Seq[DataTravel]] = db.run(dataTravelTQ.filter(_.idTravel === idTravel).result)

  override def getDataFromRuta(idRuta: Int): Future[Seq[DataTravel]] = {
    for {
      travelsID <- db.run(travelTQ.filter(_.idRuta === idRuta).map(_.idTravel).result)
      data <- db.run(dataTravelTQ.filter(_.idTravel.inSet(travelsID)).result)
    } yield {
      data
    }

  }

  val limitWithID = (limiteVelocidadTQ returning limiteVelocidadTQ.map(_.idLimite) into ((limite, newID) => limite.copy(idLimite = newID)))

  override def addLimiteVelocidad(limiteVelocidad: LimiteVelocidad): Future[Int] = db.run(limitWithID += limiteVelocidad).map(_.idLimite)

  override def getLimitesFromRuta(idRuta: Int): Future[Seq[LimiteVelocidad]] = db.run(limiteVelocidadTQ.filter(_.idRuta === idRuta).result)

}
