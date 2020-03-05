package controls
import com.thoughtworks.binding.Binding
import Binding._
import JSRoutes.JavaScriptRoutes
import OpenLayers.{ OLMapManager, Projection, View }
import PlayCallUtils.PlayAjax
import org.lrng.binding.html
import html.NodeBinding
import org.scalajs.dom.html.Button
import org.scalajs.dom.raw._
import plotly._
import element._
import layout._
import Plotly._
import org.scalajs.dom.ext.Ajax
import shared.LineStringSH
import upickle.default._

import scala.scalajs.js
import js.JSConverters._
import scala.concurrent.Future
import scala.util.{ Failure, Success }
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
object AppShowRoute {

  var mapOLOpt: Option[OLMapManager] = None
  @html
  def init(): Unit = {

    import org.scalajs.dom.document

    val rowCardElement = document.getElementById("cardsRow").asInstanceOf[HTMLDivElement]
    val rowSearchElement = document.getElementById("searchRow").asInstanceOf[HTMLDivElement]

    html.render(rowCardElement, rowCards)
    html.render(rowSearchElement, searchRow)
    //plotKmPeligrosos(Seq((0, 10), (1000, 10), (1100, 20), (1200, 10), (1400, 40)))
    mapOLOpt = Some(new OLMapManager("mapShowExactRoad"))
  }
  @html
  def getCard(colorTextTitle: String, title: String, text: String, icon: NodeBinding[Node]) = <div class="col-xl-3 col-md-6 mb-4">
                                                                                                <div class={ s"card border-left-$colorTextTitle shadow h-100 py-2" }>
                                                                                                  <div class="card-body">
                                                                                                    <div class="row no-gutters align-items-center">
                                                                                                      <div class="col mr-2">
                                                                                                        <div class={ s"text-xs font-weight-bold text-$colorTextTitle text-uppercase mb-1" }>{ title }</div>
                                                                                                        <div class="h5 mb-0 font-weight-bold text-gray-800">{ text }</div>
                                                                                                      </div>
                                                                                                      { icon }
                                                                                                    </div>
                                                                                                  </div>
                                                                                                </div>
                                                                                              </div>

  @html
  val triangleIconError: NodeBinding[HTMLDivElement] = <div class="col-auto"><i class="fa fa-exclamation-triangle fa-2x text-black-300"></i></div>

  @html
  val tachometerIcon: NodeBinding[HTMLDivElement] = <div class="col-auto"><i class="fa fa-tachometer-alt fa-2x text-black-300"></i></div>

  @html
  val carIcon: NodeBinding[HTMLDivElement] = <div class="col-auto"><i class="fa fa-car-side fa-2x text-black-300"></i></div>

  @html
  val pasajeroIcon: NodeBinding[HTMLDivElement] = <div class="col-auto"><i class="fa fa-user-edit fa-2x text-black-300"></i></div>

  val cardMetrosPeligrosos = getCard("danger", "Metros peligrosos", "45 metros", triangleIconError)
  val cardVehiculosInformales = getCard("warning", "Vehículos informales", "32 unidades", carIcon)
  val cardReducction = getCard("primary", "Cambio Km peligrosos recorridos", "Incremento 43.2 Km", tachometerIcon)
  val cardPassanger = getCard("success", "Pasajeros reportando", "104 personas", pasajeroIcon)

  @html
  val rowCards = {
    <div class="row">
      { cardMetrosPeligrosos }
      { cardVehiculosInformales }
      { cardReducction }
      { cardPassanger }
    </div>

  }

  def plotKmPeligrosos(data: Seq[(Int, Double)]): Unit = {
    val x = data.map(_._1)
    val y = data.map(_._2)
    plotlyutils.drawMap(x.toJSArray, y.toJSArray, "perfilCarretera")
    /*
    val plot = Seq(Scatter(x, y, name = "Zonas de peligro"))
    val layout = Layout(yaxis = Axis().tick)
    Plotly.plot("perfilCarretera", plot)

     */
  }

  @html
  val buttonUpdateReport: NodeBinding[Button] = {
    val button: NodeBinding[Button] = <button>UPDATE me</button>
    button.value.onclick = _ => {
      println("CLICK")
      val playCall = JavaScriptRoutes.controllers.RoutesController.provideRandomLineString
      val playAjax = new PlayAjax(playCall)
      val futResponse = playAjax.callByAjaxWithParser(dyn => read[LineStringSH](dyn.response.toString))
      updateReport(futResponse)
    }
    button
  }

  @html
  val searchRow = {
    <div class="row">
      { buttonUpdateReport }
    </div>
  }

  def updateReport(futResponse: Future[Either[String, LineStringSH]]) = {
    futResponse.onComplete {
      case Failure(error) => println(s"ERROR: ${error.toString}")
      case Success(Left(error)) => println(s"LEFT(error) = $error")
      case Success(Right(lineString)) =>
        println("SUCCESS!!")
        val mapOL = mapOLOpt.getOrElse(new OLMapManager("mapShowExactRoad"))
        mapOL.cleanLayers()
        mapOL.addRoute(lineString)
        val cxminOpt = lineString.coordinates.map(_._1).minOption
        val cxmaxOpt = lineString.coordinates.map(_._1).maxOption

        val cyminOpt = lineString.coordinates.map(_._2).minOption
        val cymaxOpt = lineString.coordinates.map(_._2).maxOption

        val cmm = for {
          cxm <- cxminOpt
          cym <- cyminOpt
        } yield {
          Array(cxm, cym)
        }
        val cMM = for {
          cxm <- cxmaxOpt
          cym <- cymaxOpt
        } yield {
          Array(cxm, cym)
        }

        for {
          min <- cmm
          max <- cMM
        } yield {

          val maxDist = Math.min(360, Math.max(0, Math.max(
            Math.abs(min(0) - max(0)),
            Math.abs(min(1) - max(1)))))
          println(s"maxDist: $maxDist")
          val k = Math.log(360d) / Math.log(2d) - Math.log(maxDist) / Math.log(2d)
          val z = Math.min(18, Math.max(0, k.toInt))
          println(s"new zoom: $z")

          val centerParam = Projection.fromLonLat(js.Array((min(0) + max(0)) / 2d, (min(1) + max(1)) / 2d))
          val viewParam = new View(js.Dynamic.literal(
            maxZoom = 18,
            center = centerParam,
            zoom = z))

          mapOL.setView(viewParam)

        }
    }
  }

}

