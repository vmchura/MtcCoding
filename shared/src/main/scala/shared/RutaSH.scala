package shared

case class RutaSH(tagName: String, coordinates: Seq[(Double, Double, Double)], longitud: Int) {
  override def toString: String = s"$tagName - ${coordinates.length} - $longitud"
}
