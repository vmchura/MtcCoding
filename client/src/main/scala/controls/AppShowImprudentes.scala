package controls

import JSRoutes.JavaScriptRoutes
import OpenLayers.{ OLMapManager, Projection, View }
import PlayCallUtils.PlayAjax
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding._
import org.lrng.binding.html
import org.lrng.binding.html.NodeBinding
import org.scalajs.dom.Event
import org.scalajs.dom.raw._
import shared.{ DataTravelSH, LineStringSH, RutaSHMeta, TravelSH }
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.util.{ Failure, Success }

object AppShowImprudentes {

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

    val rowSearchElement = document.getElementById("searchRow").asInstanceOf[HTMLDivElement]

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

  def actionWhenRutaSelected(rutaSHMeta: RutaSHMeta): Unit = {
    rutaSelected.value = Some(rutaSHMeta)

    val f0 = {
      val playCallRuta = JavaScriptRoutes.controllers.RutaController.getLineStringRuta(rutaSHMeta.idRuta)
      val playAjaxRuta = new PlayAjax(playCallRuta)
      playAjaxRuta.callByAjaxWithParser(dyn => read[LineStringSH](dyn.response.toString))
    }

    updateMapa(f0)

    val f1 = for {
      _ <- f0
      futResponse <- {
        val playCall = JavaScriptRoutes.controllers.ReportController.findDangerousVehiclesLive(rutaSHMeta.idRuta)
        val playAjax = new PlayAjax(playCall)
        playAjax.callByAjaxWithParser(dyn => read[Seq[(TravelSH, DataTravelSH)]](dyn.response.toString))
      }
    } yield {
      futResponse

    }

    updateReport(f1, (255, 0, 0))

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

  def updateReport(futResponse: Future[Either[String, Seq[(TravelSH, DataTravelSH)]]], color: (Int, Int, Int)) = {
    futResponse.onComplete {
      case Failure(error) => println(s"ERROR: ${error.toString}")
      case Success(Left(error)) => println(s"LEFT(error) = $error")
      case Success(Right(seqTravelDataTravel)) =>
        println("SUCCESS!!")

        val mapOL = mapOLOpt.getOrElse(new OLMapManager("mapShowExactRoad"))
        mapOL.cleanFantasyLayers()
        println(seqTravelDataTravel.mkString("\n"))
        seqTravelDataTravel.foreach { case (t, d) => mapOL.addFantasyLayer(t, d, (255, 0, 0)) }
    }
  }

  def updateMapa(futResponse: Future[Either[String, LineStringSH]]) = {
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

