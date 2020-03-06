package scalakml.io

import PeruRoutes.{ BasicPoint, Coordinates }
import org.scalatest.FlatSpec
import scalakml.kml.{ Coordinate, Document, Feature, Folder, Geometry, Kml, LineString, MultiGeometry, Placemark, Point }
import shared.RutaSH

class KmlFileReaderTest extends FlatSpec {

  behavior of "Reading kml"
  it should "work fine on points " in {

    // read a kml file into a kml root object.
    // Note the default extractor (as shown) can be replaced by your own KmlExtractor
    // see KmlExtractor trait in KmlFileReader
    val kmlOption: Option[Kml] = new KmlFileReader().getKmlFromFile("/home/vmchura/Documents/002.DescargasFirefox/RVD_Ptos.kml")

    assert(kmlOption.isDefined)
    val kml = kmlOption.get
    //println(kml.toString)
    // get the placemark point
    //  val point = placemark.geometry.get.asInstanceOf[Point]
    // print the point coordinates
    //  println("coordinate: " + point.coordinates)

  }

  it should "work fine on roads" in {
    def transformPM(placemark: (String, Seq[Coordinate])): RutaSH = {
      val (tag, geom) = placemark

      val coordinates = geom.map { c =>
        (c.longitude.get, c.latitude.get)
      }

      RutaSH(tag, coordinates)

    }
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
      tagSeqLS.map(k => (k._1, k._2.filter(validLineString).map(_.coordinates.get).maxByOption(_.length))).flatMap { r =>
        r._2.map { q =>
          transformPM((r._1, q))
        }

      }
    }

    def getLength(coordinate: Seq[(Double, Double)]): Double = {
      coordinate.foldLeft((0d, BasicPoint(0, 0))) {
        case ((acumLeft, prevP), p) => {
          val bp = Coordinates(p._2, p._1).toPoint()
          (acumLeft + bp.dist(prevP), bp)
        }
      }._1
    }

    rutas.foreach { ruta =>
      println(s"${ruta.tagName} ${ruta.coordinates.length} ${getLength(ruta.coordinates)}")
    }

  }
}
