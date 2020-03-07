package shared
import upickle.default.{ macroRW, ReadWriter => RW }
case class TravelSH(placa: String, lng: Double, lat: Double)
object TravelSH {
  implicit val rw: RW[TravelSH] = macroRW
}
