package models.daos

import models._
import shared.{ LineStringSH, RutaSH, RutaSHMeta }

import scala.concurrent.Future

trait MobileDAO {
  // ---------- RUTA
  def addRuta(ruta: Ruta): Future[Int]
  def addCoordinates(rutaCoordinates: Seq[RutaCoordinates]): Future[Int]
  def getRutaToDraw(rutaID: Int): Future[Option[LineStringSH]]

  def getRuta(idRuta: Int): Future[Option[Ruta]]
  def getCoordinates(idRuta: Int): Future[Seq[RutaCoordinates]]
  def getAllRutas(): Future[Seq[Ruta]]
  def getAllRoutsSH(): Future[Map[Int, RutaSH]]
  def getAllRutasSHMeta(): Future[Seq[RutaSHMeta]]

  // --------- PASAJERo

  def addPasajero(pasajero: Pasajero): Future[Int]
  def getPasajero(idPasajero: Int): Future[Option[Pasajero]]

  // ------- TRAVEL

  def addTravel(travel: Travel): Future[Int]
  def getTravel(idTravel: Int): Future[Option[Travel]]
  def addDataTravel(dataTravel: DataTravel): Future[Int]
  def getDataFromTravel(idTravel: Int): Future[Seq[DataTravel]]
  def getDataFromRuta(idRuta: Int): Future[Seq[DataTravel]]

  // -----   LIMITE VELOCIDAD
  def addLimiteVelocidad(limiteVelocidad: LimiteVelocidad): Future[Int]
  def getLimitesFromRuta(idRuta: Int): Future[Seq[LimiteVelocidad]]

  // ------- REPORTE

  //DANGEROUS

  def findDangerousVehiclesLive(idRuta: Int): Future[Seq[(Travel, DataTravel)]]
  def findDangerousVehicles(idRuta: Int): Future[Seq[Travel]]

  //INFORMALES

  def findInformalVehicles(idRuta: Int): Future[Seq[Travel]]
  def findInformalVehiclesLive(idRuta: Int): Future[Seq[(Travel, DataTravel)]]

  //SEGMENTOS INFORMALES

  def findRutasInformales(): Future[Seq[(String, Int, Seq[LineStringSH], LineStringSH)]]
  //SEGMENTOS CRITICOS

  def findSegmentosCriticos(rutaID: Int): Future[Option[(String, Int, Seq[LineStringSH], LineStringSH)]]

}
