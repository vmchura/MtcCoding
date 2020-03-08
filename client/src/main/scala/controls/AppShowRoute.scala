package controls
import com.thoughtworks.binding.Binding
import Binding._
import JSRoutes.JavaScriptRoutes
import OpenLayers.{ OLMapManager, Projection, View }
import PlayCallUtils.{ PlayAjax, PlayCall }
import org.lrng.binding.html
import html.NodeBinding
import org.scalajs.dom.html.Button
import org.scalajs.dom.raw._
import plotly._
import element._
import layout._
import Plotly._
import com.thoughtworks.binding
import org.scalajs.dom.Event
import org.scalajs.dom.ext.Ajax
import scalaz.Bind
import shared.{ LineStringSH, RutaSHMeta }
import upickle.default._

import scala.scalajs.js
import js.JSConverters._
import scala.concurrent.Future
import scala.util.{ Failure, Success }
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
object AppShowRoute {

  var mapOLOpt: Option[OLMapManager] = None
  val rutaSelected = Var[Option[RutaSHMeta]](None)
  val rutasMeta = Vars[RutaSHMeta]()

  val textPrefix = Var[String]("")

  val vehiculosInformales = Var[String]("~")
  val personasReportando = Var[String]("~")

  @html
  val inputPrefix = {
    val in: NodeBinding[HTMLInputElement] = <input type="email" id="inputrute" class="form-control" data:aria-describedby="ruta" placeholder="Escoja ruta"> </input>
    in.value.onchange = _ => {
      println("CHANGED")
      textPrefix.value = in.value.value
      println("CHANGED to " + textPrefix.value)
    }

    <div class="form-group">
      <label for="inputrute">Ruta</label>
      { in }
    </div>

  }

  @html
  val rutasByPrefix = {
    val xy: BindingSeq[RutaSHMeta] = for {
      e <- rutasMeta
      if e.tagName.toLowerCase().startsWith(textPrefix.bind)
    } yield {
      e
    }
    xy
  }

  @html
  def init(): Unit = {

    import org.scalajs.dom.document

    val rowCardElement = document.getElementById("cardsRow").asInstanceOf[HTMLDivElement]
    val rowSearchElement = document.getElementById("searchRow").asInstanceOf[HTMLDivElement]

    html.render(rowCardElement, rowCards)
    html.render(rowSearchElement, searchRow)
    //plotKmPeligrosos(Seq((0, 10), (1000, 10), (1100, 20), (1200, 10), (1400, 40)))
    mapOLOpt = Some(new OLMapManager("mapShowExactRoad"))

    val pc = JSRoutes.JavaScriptRoutes.controllers.RutaController.getRutasAndMetadata()
    val ac = new PlayAjax(pc)
    val f: Future[Either[String, Seq[RutaSHMeta]]] = ac.callByAjaxWithParser(dyn => read[Seq[RutaSHMeta]](dyn.response.toString))
    f.onComplete {
      case Success(Left(error)) => println("ERROR DOWNLOAD rutaSHMeta")
      case Success(Right(value)) => rutasMeta.value ++= value
      case Failure(exception) => println(s"ERROR EXCEPTION: ${exception.toString}")
    }

  }
  @html
  def getCard(colorTextTitle: String, title: String, text: Var[String], icon: NodeBinding[Node]) = <div class="col-xl-6 col-md-6 mb-4">
                                                                                                     <div class={ s"card border-left-$colorTextTitle shadow h-100 py-2" }>
                                                                                                       <div class="card-body">
                                                                                                         <div class="row no-gutters align-items-center">
                                                                                                           <div class="col mr-2">
                                                                                                             <div class={ s"text-xs font-weight-bold text-$colorTextTitle text-uppercase mb-1" }>{ title }</div>
                                                                                                             <div class="h5 mb-0 font-weight-bold text-gray-800">{ text.bind }</div>
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

  //val cardMetrosPeligrosos = getCard("danger", "Metros peligrosos", "45 metros", triangleIconError)
  val cardVehiculosInformales = getCard("warning", "Vehículos informales", vehiculosInformales, carIcon)
  //val cardReducction = getCard("primary", "Cambio Km peligrosos recorridos", "Incremento 43.2 Km", tachometerIcon)
  val cardPassanger = getCard("success", "Pasajeros reportando", personasReportando, pasajeroIcon)

  @html
  val rowCards = {
    <div class="row">
      { cardVehiculosInformales }
      { cardPassanger }
    </div>

  }

  def plotKmPeligrosos(data: Seq[(Int, Double)]): Unit = {
    val x = data.map(_._1)
    val y = data.map(_._2)
    plotlyutils.drawMap(x.toJSArray, y.toJSArray, "perfilCarretera")

  }

  def actionWhenRutaSelected(rutaSHMeta: RutaSHMeta): Unit = {
    rutaSelected.value = Some(rutaSHMeta)
    val playCall = JavaScriptRoutes.controllers.ReportController.findSegmentosCriticos(rutaSHMeta.idRuta)
    val playAjax: PlayAjax[Option[(String, Int, Seq[LineStringSH], LineStringSH)]] = new PlayAjax(playCall)
    val futResponse = playAjax.callByAjaxWithParser(dyn => read[Option[(String, Int, Seq[LineStringSH], LineStringSH)]](dyn.response.toString))
    updateReport(futResponse)

    val playCallCards = JavaScriptRoutes.controllers.ReportController.getPasajerosEInformales(rutaSHMeta.idRuta)
    val playAjaxCards = new PlayAjax(playCallCards)
    val futResponseCards = playAjaxCards.callByAjaxWithParser(dyn => read[(Int, Int)](dyn.response.toString()))
    updateCards(futResponseCards)
  }

  def updateCards(futResponse: Future[Either[String, (Int, Int)]]): Unit = {
    futResponse.onComplete {
      case Success(Right((numPasajeros, numInformales))) =>
        personasReportando.value = numPasajeros.toString + " personas"
        vehiculosInformales.value = numInformales.toString + " vehículos"
      case _ => println("ERROR CARDS")
    }
  }

  @html
  val rutasDropDown = Binding {
    <div class="form-group">
      <label for="droprutas">Seleccione</label>
      <div class="dropdown" id="droprutas">
        <button class="btn btn-info dropdown-toggle btn-block" type="button" id="dropdownMenu0" data:data-toggle="dropdown" data:aria-haspopup="true" data:aria-expanded="false">
          { rutaSelected.bind.map(_.tagName).getOrElse("Seleccionar proyecto") }
        </button>
        <div class="dropdown-menu" data:aria-labelledby="dropdownMenu0">
          {
            import scalaz.std.list._
            for (rutaSH <- rutasByPrefix) yield {

              <button class="dropdown-item btn-block" type="button" onclick={ _: Event => actionWhenRutaSelected(rutaSH) }>
                { rutaSH.tagName }
              </button>
            }
          }
        </div>
      </div>
    </div>.bind
  }

  @html
  val searchRow = {
    <div class="row">
      <div class="col4">
        { inputPrefix }
      </div>
      <div class="col4">
        { rutasDropDown }
      </div>
    </div>
  }

  def updateReport(futResponse: Future[Either[String, Option[(String, Int, Seq[LineStringSH], LineStringSH)]]]) = {
    futResponse.onComplete {
      case Failure(error) => println(s"ERROR: ${error.toString}")
      case Success(Left(error)) => println(s"LEFT(error) = $error")
      case Success(Right(Some((tagRuta, porcentajeInformalidad, informales, lineString)))) =>
        println("SUCCESS!!")

        plotlyutils.drawPie(porcentajeInformalidad)

        val mapOL = mapOLOpt.getOrElse(new OLMapManager("mapShowExactRoad"))
        mapOL.cleanLayers()
        mapOL.addRoute(lineString)
        println(s"informales: ${informales.length}")
        informales.foreach { mapOL.addRoute }

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

