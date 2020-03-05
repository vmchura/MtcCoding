package OpenLayers

import shared.LineStringSH

import scala.scalajs.js
import scala.scalajs.js.annotation.{ JSGlobal, JSGlobalScope }

@js.native
@JSGlobalScope
object OLMap extends js.Object {

  //def updateMap(lines: Seq[LineStringSH]): Unit = js.native
  def createMap(target: String): js.Object = js.native
  def addRoute(map: js.Object): Unit = js.native
  def addRouteFromFeauture(map: js.Object, routeFeature: js.Object): Unit = js.native
  def addRouteFromRoute(map: js.Object, route: js.Object): Unit = js.native
}
