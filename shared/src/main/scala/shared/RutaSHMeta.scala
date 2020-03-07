package shared

import upickle.default.{ macroRW, ReadWriter => RW }

case class RutaSHMeta(idRuta: Int, tagName: String)
object RutaSHMeta {

  implicit val rw: RW[RutaSHMeta] = macroRW
}
