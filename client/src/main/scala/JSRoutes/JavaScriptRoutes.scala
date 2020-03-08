package JSRoutes

import PlayCallUtils.PlayCall
import shared.{ DataTravelSH, LineStringSH, RutaSHMeta, TravelSH }

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
  val RutaController: TRutaController = js.native
  val ReportController: TReportController = js.native
}

@js.native
trait TRoutesController extends js.Object {
  def provideRandomLineString(): PlayCall[LineStringSH] = js.native
}

@js.native
trait TRutaController extends js.Object {
  def getRutasAndMetadata(): PlayCall[Seq[RutaSHMeta]] = js.native
}

@js.native
trait TReportController extends js.Object {
  def findDangerousVehiclesLive(idRuta: Int): PlayCall[Seq[(TravelSH, DataTravelSH)]] = js.native
  def findDangerousVehicles(idRuta: Int): PlayCall[Seq[TravelSH]] = js.native
  def findInformalVehicles(idRuta: Int): PlayCall[Seq[TravelSH]] = js.native
  def findInformalVehiclesLive(idRuta: Int): PlayCall[Seq[(TravelSH, DataTravelSH)]] = js.native
  def findRutasInformales(): PlayCall[Seq[(String, Int, Seq[LineStringSH], LineStringSH)]] = js.native
  def findSegmentosCriticos(idRuta: Int): PlayCall[Option[(String, Int, Seq[LineStringSH], LineStringSH)]] = js.native
  def getDataChartVelocity(idRuta: Int): PlayCall[Seq[Int]] = js.native
  def getPasajerosEInformales(idRuta: Int): PlayCall[(Int, Int)] = js.native

}
