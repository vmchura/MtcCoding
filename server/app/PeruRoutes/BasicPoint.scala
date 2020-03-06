package PeruRoutes

case class BasicPoint(x: Double, y: Double) {
  def dist(ohter: BasicPoint): Double = {
    val dx = x - ohter.x
    val dy = y - ohter.y
    Math.min(Math.sqrt(dx * dx + dy * dy), 8d)
  }
}
