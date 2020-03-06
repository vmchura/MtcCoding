package models.mappers

import models._
import models.daos.CustomColumnTypes
import models.mappers.CelaRefinedProfile
import org.joda.time.DateTime
trait RouteMobileMapping {
  protected val profile: CelaRefinedProfile
  protected val customColumnTypes: CustomColumnTypes

  import profile.api._
  import customColumnTypes.jodaDateTimeType
  class RutaTable(tag: Tag) extends Table[Ruta](tag, "mtc_ruta") {
    def idRuta = column[Int]("idRuta", O.PrimaryKey, O.AutoInc)
    def tagRuta = column[String]("tagRuta", O.Length(32))
    def progFin = column[Int]("progFin")

    override def * = (idRuta, tagRuta, progFin).mapTo[Ruta]
  }

  class RutaCoordinatesTable(tag: Tag) extends Table[RutaCoordinates](tag, "mtc_rutacoordinates") {
    def idCoordinate = column[Int]("idCoordinate", O.PrimaryKey, O.AutoInc)
    def lng = column[Double]("lng")
    def lat = column[Double]("lat")
    def prog = column[Int]("prog")
    def idRuta = column[Int]("idRuta")
    override def * = (idCoordinate, lng, lat, prog, idRuta).mapTo[RutaCoordinates]

    def ruta = foreignKey("mtc_coord_ruta", idRuta, rutaTQ)(_.idRuta, onDelete = ForeignKeyAction.Cascade, onUpdate = ForeignKeyAction.Cascade)
  }

  class PasajeroTable(tag: Tag) extends Table[Pasajero](tag, "mtc_pasajero") {
    def idPasajero = column[Int]("idPasajero", O.PrimaryKey, O.AutoInc)
    override def * = (idPasajero).mapTo[Pasajero]
  }

  class TravelTable(tag: Tag) extends Table[Travel](tag, "mtc_travel") {
    def idTravel = column[Int]("idTravel", O.PrimaryKey, O.AutoInc)
    def startTime = column[DateTime]("startTime")
    def placa = column[String]("placa", O.Length(32))
    def idRuta = column[Int]("idRuta")
    def isLive = column[Boolean]("isLive")
    def direction = column[Boolean]("direction")
    def idPasajero = column[Int]("idPasajero")
    def infringio = column[Boolean]("infringio")
    override def * = (idTravel, startTime, placa, idRuta, isLive, direction, idPasajero, infringio).mapTo[Travel]

    def ruta = foreignKey("mtc_travel_ruta", idRuta, rutaTQ)(_.idRuta, onDelete = ForeignKeyAction.Cascade, onUpdate = ForeignKeyAction.Cascade)
    def pasajero = foreignKey("mtc_travel_pasajero", idPasajero, pasajeroTQ)(_.idPasajero, onDelete = ForeignKeyAction.Cascade, onUpdate = ForeignKeyAction.Cascade)
  }

  class DataTravelTable(tag: Tag) extends Table[DataTravel](tag, "mtc_datatravel") {
    def idDataTable = column[Int]("idDataTable", O.PrimaryKey, O.AutoInc)
    def dataTime = column[DateTime]("dataTime")
    def idTravel = column[Int]("idTravel")
    def prog = column[Int]("prog")
    def velocidad = column[Int]("velocidad")

    override def * = (idDataTable, dataTime, idTravel, prog, velocidad).mapTo[DataTravel]

    def travel = foreignKey("mtc_datatravel_travel", idTravel, travelTQ)(_.idTravel, onDelete = ForeignKeyAction.Cascade, onUpdate = ForeignKeyAction.Cascade)

  }

  class LimiteVelocidadTable(tag: Tag) extends Table[LimiteVelocidad](tag, "mtc_limitevelocidad") {
    def idLimite = column[Int]("idLimite", O.PrimaryKey, O.AutoInc)
    def idRuta = column[Int]("idRuta")
    def progIni = column[Int]("progIni")
    def progFin = column[Int]("progFin")
    def limite = column[Int]("limite")
    override def * = (idLimite, idRuta, progIni, progFin, limite).mapTo[LimiteVelocidad]
    def ruta = foreignKey("mtc_limite_ruta", idRuta, rutaTQ)(_.idRuta, onDelete = ForeignKeyAction.Cascade, onUpdate = ForeignKeyAction.Cascade)
  }

  val limiteVelocidadTQ = TableQuery[LimiteVelocidadTable]
  val rutaTQ = TableQuery[RutaTable]
  val rutaCoordiantesTQ = TableQuery[RutaCoordinatesTable]
  val pasajeroTQ = TableQuery[PasajeroTable]
  val travelTQ = TableQuery[TravelTable]
  val dataTravelTQ = TableQuery[DataTravelTable]

}
