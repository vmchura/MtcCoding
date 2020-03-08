package OpenLayers

import shared.{ DataTravelSH, LineStringSH, TravelSH }

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
  var listBufferFantasyLayerAppend = ListBuffer.empty[Vector]

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
  def cleanFantasyLayers(): Unit = {
    listBufferFantasyLayerAppend.foreach(olmap.removeLayer)
    listBufferFantasyLayerAppend.clear()
  }
  def addFantasyLayer(travelSH: TravelSH, dataTravelSH: DataTravelSH, colorParam: (Int, Int, Int)): Unit = {
    val (r, g, b) = colorParam

    val geoMarker = new Style(js.Dynamic.literal("image" ->
      new CircleStyle(js.Dynamic.literal(
        "radius" -> 7,
        "fill" -> new FillStyle(js.Dynamic.literal("color" -> js.Array(r, g, b, 0.8))),
        "stroke" -> new Stroke(js.Dynamic.literal("color" -> "white", "width" -> 2)),
        "text" -> new Text(js.Dynamic.literal(
          "font" -> "12px Calibri,sans-serif",
          "fill" -> new FillStyle(js.Dynamic.literal("color" -> "#000")),
          "stroke" -> new Stroke(js.Dynamic.literal("color" -> "#fff", "width" -> 2)),
          "text" -> travelSH.placa))))))

    val vehicleMarker = new Feature(js.Dynamic.literal("type" -> "geoMarker", "geometry" -> {
      new Point(Projection.fromLonLat(Seq(dataTravelSH.lng, dataTravelSH.lat).toJSArray))
    }, "style" -> geoMarker))

    /**
     * var vectorLayer = new VectorLayer({
     * source: new VectorSource({
     * features: [routeFeature, geoMarker, startMarker, endMarker]
     * }),
     * style: function(feature) {
     * // hide geoMarker if animation is active
     * if (animating && feature.get('type') === 'geoMarker') {
     * return null;
     * }
     * return styles[feature.get('type')];
     * }
     * });
     */
    val vectorLayer = new OpenLayers.Vector(js.Dynamic.literal(
      source = new VectorSource(js.Dynamic.literal(features = js.Array(vehicleMarker))),
      style = geoMarker))

    olmap.addLayer(vectorLayer)
    listBufferFantasyLayerAppend.append(vectorLayer)

  }
  def setView(view: View): Unit = olmap.setView(view)
}
