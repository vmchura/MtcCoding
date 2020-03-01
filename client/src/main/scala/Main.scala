import org.lrng.binding.html
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.{ Event, HTMLCollection, HTMLFormElement, Node }
import shared.SharedMessages

import scala.scalajs.js
import scala.scalajs.js.annotation.{ JSExport, _ }
import scala.xml.NodeBuffer

@JSExportTopLevel("Main") object Main {

  def main(args: Array[String]): Unit = {
    println("gg")
  }

  @html
  val myDiv = <div>Hola desde JS: { SharedMessages.message }</div>

  @JSExport("renderDiv")
  def renderDiv(containerID: String): Unit = {
    html.render(dom.document.getElementById(containerID), myDiv)
  }
}