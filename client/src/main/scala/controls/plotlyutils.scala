package controls

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobalScope
import js.JSConverters._
@js.native
@JSGlobalScope
object plotlyutils extends js.Object {

  //def updateMap(lines: Seq[LineStringSH]): Unit = js.native
  def drawMap(x: js.Array[Int], promedio: js.Array[Double], limite: js.Array[Double], targetParam: String): js.Object = js.native
  def drawPie(informales: Int): Unit = js.native
}
