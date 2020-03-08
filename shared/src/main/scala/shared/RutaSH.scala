package shared

case class RutaSH(tagName: String, coordinates: Array[(Double, Double, Double)], longitud: Int) {
  override def toString: String = s"$tagName - ${coordinates.length} - $longitud"
  private def findClosestInt(prog: Double): Int = {
    val debug = Math.abs(prog - 42200.0) < 1e-6
    var left = 0
    var right = coordinates.length - 1
    def f: Int => Double = pos => coordinates(pos)._3

    if (f(left) > prog) {
      left
    } else {
      if (f(right) < prog) {
        right
      } else {
        while (right - left > 1) {
          val mid = (left + right) / 2
          if (f(mid) > prog) {
            right = mid
          } else {
            left = mid
          }
        }
        left
      }
    }
  }
  def findClosestPoint(prog: Double): (Double, Double) = {
    def g: Int => (Double, Double) = pos => (coordinates(pos)._1, coordinates(pos)._2)
    g(findClosestInt(prog))
  }

  def findRange(progIni: Double, progFin: Double): Seq[(Double, Double)] = {
    val a = findClosestInt(progIni)
    val b = findClosestInt(progFin)
    val (ini, fin) = (Math.min(a, b), Math.max(a, b))
    coordinates.slice(ini, fin).map { case (w, z, _) => (w, z) }
  }
}
