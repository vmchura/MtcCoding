package OpenLayers

import shared.LineStringSH

import scala.scalajs.js
import js.JSConverters._
import scala.collection.mutable.ListBuffer

class OLMapManager(targetParam: String) {

  val osm = new OSM(js.Dynamic.literal())
  val openCycleMapLayer = new Tile(js.Dynamic.literal(source = osm))

  val viewParam = new View(js.Dynamic.literal(maxZoom = 18, center = js.Array(-8486558.521727294, -1111420.8471308986), zoom = 15))

  val olmap = new OLMapRequest(js.Dynamic.literal(
    layers = js.Array(openCycleMapLayer),
    target = targetParam,
    view = viewParam))

  var listBufferLayerAppend = ListBuffer.empty[Vector]

  def addRoute(lineStringSH: LineStringSH): Vector = {

    val coordinatesJS = lineStringSH.coordinates.map {
      case (a, b) => Projection.fromLonLat(Seq(a, b).toJSArray)
    }

    val route = new LineString(coordinatesJS.toJSArray)

    val routeFeature = new Feature(js.Dynamic.literal("type" -> "route", "geometry" -> route))

    val (r, g, b) = lineStringSH.color
    val vectorLayer = new OpenLayers.Vector(js.Dynamic.literal(
      source = new VectorSource(js.Dynamic.literal(features = js.Array(routeFeature))),
      style = new Style(js.Dynamic.literal(stroke = new Stroke(js.Dynamic.literal(width = lineStringSH.width, color = js.Array(r, g, b, 0.8)))))))

    olmap.addLayer(vectorLayer)
    listBufferLayerAppend.append(vectorLayer)
    vectorLayer

  }
  def cleanLayers(): Unit = {
    listBufferLayerAppend.foreach(olmap.removeLayer)
    listBufferLayerAppend.clear()

  }
  def setView(view: View): Unit = olmap.setView(view)
}
