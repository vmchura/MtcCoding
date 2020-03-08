
import OpenLayers.{ Feature, LineString, OLMap, OLMapManager, OLMapRequest, OSM, Stroke, Style, Tile, Vector, VectorSource, View }
import controls.{ AppShowImprudentes, AppShowRoute, AppShowSegmentosCriticos }
import org.lrng.binding.html
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.{ Event, HTMLCollection, HTMLDivElement, HTMLFormElement, Node }
import shared.{ LineStringSH, SharedMessages }

import scala.scalajs.js
import scala.scalajs.js.annotation.{ JSExport, _ }
import scala.xml.NodeBuffer
import org.scalajs.dom._

import scala.scalajs.js
import js.annotation.JSExport
import org.scalajs.dom

import js.JSConverters._

@JSExportTopLevel("Main") object Main {

  def main(args: Array[String]): Unit = {

    window.onload = _ => {
      val e = document.getElementById("EnableAppShowRoute")
      if (e != null) {
        AppShowRoute.init()
        println("EnableAppShowRoute !!")
      } else {
        println("DISABLE AppShowRoute !!")
      }

      val v = document.getElementById("EnableAppShowPuntosCriticos")
      if (v != null) {
        AppShowSegmentosCriticos.init()
        println("EnableAppShowPuntosCriticos !!")
      } else {
        println("DISABLE AppShowPuntosCriticos !!")
      }

      val imp = document.getElementById("EnableAppShowImprudentes")
      if (imp != null) {
        AppShowImprudentes.init()
        println("EnableAppShowImprudentes !!")
      } else {
        println("DISABLE EnableAppShowImprudentes !!")
      }
    }

  }

  @JSExport("initOSM")
  def initOSM(targetParam: String): Unit = {

  }
  @JSExport("drawButtonManageMap")
  def drawButtonManageMap(map: OLMapRequest): Unit = {

  }

}