package PeruRoutes

import scalakml.io.KmlFileReader
import scalakml.kml._
import shared.RutaSH

import scala.collection.mutable

object PeruRoutes {
  def getLength(coordinate: Seq[(Double, Double)]): Double = {
    coordinate.foldLeft((0d, BasicPoint(0, 0))) {
      case ((acumLeft, prevP), p) => {
        val bp = Coordinates(p._2, p._1).toPoint()
        (acumLeft + bp.dist(prevP), bp)
      }
    }._1
  }

  def transformPM(placemark: (String, Seq[Coordinate])): RutaSH = {
    val (tag, geom) = placemark

    val coordinates: Seq[(Double, Double)] = geom.map { c =>
      (c.longitude.get, c.latitude.get)
    }
    val validCoordinates = coordinates.scanLeft((BasicPoint(0d, 0d), (0d, 0d, -8d))) {
      case ((prevpoint, (_, _, acum)), (lng, lat)) => {
        val bp = Coordinates(lat, lng).toPoint()
        val nd = acum + prevpoint.dist(bp)
        (bp, (lng, lat, nd))
      }
    }.tail.map(_._2)

    RutaSH(tag, validCoordinates, validCoordinates.last._3.toInt)

  }
  /*
  val rutas: Seq[RutaSH] = {
    val kmlOption: Option[Kml] = new KmlFileReader().getKmlFromFile("/home/vmchura/Documents/002.DescargasFirefox/RVD_Eje.kml")

    //println(kml.toString)

    val folders: Seq[Feature] = (kmlOption.flatMap { _.feature } match {
      case Some(d: Document) => {
        assert(d.features.forall {
          case _: Folder => true
          case _ => false
        })
        d.features.map(_.asInstanceOf[Folder].features)
      }
      case _ => Nil
    }).flatten

    val tagMultGeo = ((folders.map(_.asInstanceOf[Placemark])).map(pm => (pm.featurePart.name.getOrElse("Ruta sin nombre"), pm.geometry.get))).map(k => (k._1, k._2.asInstanceOf[MultiGeometry]))

    val tagSeqLS: Seq[(String, Seq[LineString])] = tagMultGeo.map(k => (k._1, k._2.geometries.tail.map(_.asInstanceOf[LineString])))
    def validLineString(ls: LineString): Boolean = {
      ls.coordinates match {
        case Some(coordinates) =>
          coordinates.count(c => c.latitude.isDefined && c.longitude.isDefined) > 1000
        case None => false
      }
    }

    //tagSeqLS.map(k => (k._1, k._2.filter(validLineString).map(_.coordinates.get)))
    val mem: Seq[RutaSH] = tagSeqLS.map(k => (k._1, k._2.filter(validLineString).map(_.coordinates.get).maxByOption(_.length))).flatMap { r =>
      r._2.map { q =>
        transformPM((r._1, q))
      }

    }

    val mapMemoryTag = mutable.Map.empty[String, Int]
    mem.map(r => {
      if (mapMemoryTag.contains(r.tagName)) {
        mapMemoryTag(r.tagName) = mapMemoryTag(r.tagName) + 1
        r.copy(tagName = r.tagName + ("/" + ('a' + mapMemoryTag(r.tagName)).toChar.toString))
      } else {
        mapMemoryTag += r.tagName -> 0
        r
      }
    })

  }

  val mapRutasByID = mutable.Map.empty[Int, RutaSH]
  println(rutas.mkString("\n"))
*/
  var rutasOnMemory = Map.empty[Int, RutaSH]
  def randomRoute(): RutaSH = ???

}
