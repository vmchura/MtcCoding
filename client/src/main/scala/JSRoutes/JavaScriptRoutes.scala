package JSRoutes

import PlayCallUtils.PlayCall
import shared.LineStringSH

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("jsRoutes")
object JavaScriptRoutes extends js.Object {
  def controllers: Tcontrollers = js.native
}

@js.native
trait Tcontrollers extends js.Object {
  val RoutesController: TRoutesController = js.native
}

@js.native
trait TRoutesController extends js.Object {
  def provideRandomLineString(): PlayCall[LineStringSH] = js.native
}
