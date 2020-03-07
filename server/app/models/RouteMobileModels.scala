package models

import org.joda.time.DateTime
import shared.{ DataTravelSH, TravelSH }
import PeruRoutes.PeruRoutes.rutasOnMemory
case class Ruta(idRuta: Int, tagRuta: String, progFin: Int)
case class RutaCoordinates(idCoordinate: Int, lng: Double, lat: Double, prog: Int, idRuta: Int)
case class Pasajero(idPasajero: Int)
case class Travel(
  idTravel: Int,
  startTime: DateTime,
  placa: String,
  esInformal: Boolean,
  idRuta: Int,
  isLive: Boolean,
  direction: Boolean,
  initialPosition: Int,
  idPasajero: Int, infringio: Boolean) {
  def toTravelSH(): TravelSH = {
    val (lng, lat) = if (rutasOnMemory.contains(idRuta)) {
      val ruta = rutasOnMemory(idRuta)
      ruta.findClosestPoint(initialPosition.toDouble)

    } else {
      (0d, 0d)
    }
    TravelSH(placa, lng, lat)

  }
}

/**
 *
 * @param idDataTravel
 * @param dataTime
 * @param idTravel
 * @param prog
 * @param velocidad VELCIDAD NEGATIVA PARA INDICAR QUE YA SE TERMINO EL VIAJE
 */
case class DataTravel(idDataTravel: Int, dataTime: DateTime, idTravel: Int, prog: Int, velocidad: Int) {
  def toDataTravelSH(): DataTravelSH = DataTravelSH(dataTime.getMillis, prog, velocidad)
}
case class LimiteVelocidad(idLimite: Int, idRuta: Int, progIni: Int, progFin: Int, limite: Int)

