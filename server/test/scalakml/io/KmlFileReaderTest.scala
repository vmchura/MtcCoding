package scalakml.io

import org.scalatest.FlatSpec
import scalakml.kml.{Kml, Placemark}

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
    println("....ReadExample1 start...\n")

    // read a kml file into a kml root object.
    // Note the default extractor (as shown) can be replaced by your own KmlExtractor
    // see KmlExtractor trait in KmlFileReader
    val kmlOption: Option[Kml] = new KmlFileReader().getKmlFromFile("/home/vmchura/Documents/002.DescargasFirefox/RVD_Eje.kml")

    assert(kmlOption.isDefined)
    val kml = kmlOption.get
    println(kml.toString)
    // get the placemark point
    //  val point = placemark.geometry.get.asInstanceOf[Point]
    // print the point coordinates
    //  println("coordinate: " + point.coordinates)

    println("\n....ReadExample1 done...")
  }
}
