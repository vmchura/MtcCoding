package shared

case class RutaSH(tagName: String, coordinates: Array[(Double, Double, Double)], longitud: Int) {
  override def toString: String = s"$tagName - ${coordinates.length} - $longitud"
  def findClosestPoint(prog: Double): (Double, Double) = {
    var left = 0
    var right = coordinates.length - 1
    def f: Int => Double = pos => coordinates(pos)._3
    def g: Int => (Double, Double) = pos => (coordinates(pos)._1, coordinates(pos)._2)
    if (f(left) > prog) {
      g(left)
    } else {
      if (f(right) < prog) {
        g(right)
      } else {
        while (right - left > 1) {
          val mid = (left + right) / 2
          if (f(mid) > prog) {
            left = mid
          } else {
            right = mid
          }
        }
        g(left)
      }
    }

  }
}
