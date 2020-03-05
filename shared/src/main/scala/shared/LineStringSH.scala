package shared
import upickle.default.{ macroRW, ReadWriter => RW }
case class LineStringSH(color: (Int, Int, Int), width: Double, coordinates: Seq[(Double, Double)]) {

}
object LineStringSH {

  implicit val rw: RW[LineStringSH] = macroRW
}
