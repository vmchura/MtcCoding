package shared

import upickle.default.{ macroRW, ReadWriter => RW }
case class DataTravelSH(dataTime: Long, lng: Double, lat: Double, velocidad: Int)
object DataTravelSH {
  implicit val rw: RW[DataTravelSH] = macroRW
}
