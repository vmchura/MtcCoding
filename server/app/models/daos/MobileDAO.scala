package models.daos

import models._

import scala.concurrent.Future

trait MobileDAO {
  // ---------- RUTA
  def addRuta(ruta: Ruta): Future[Int]
  def addCoordinates(rutaCoordinates: Seq[RutaCoordinates]): Future[Int]

  def getRuta(idRuta: Int): Future[Option[Ruta]]
  def getCoordinates(idRuta: Int): Future[Seq[RutaCoordinates]]

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

}
