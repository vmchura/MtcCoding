package controllers

import javax.inject.Inject
import play.api.http.MimeTypes
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.api.routing._

class JavaScriptRouter @Inject() (components: ControllerComponents)
  extends AbstractController(components) with I18nSupport {

  def javascriptRoutes = Action { implicit request =>

    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.RoutesController.provideRandomLineString,
        routes.javascript.ReportController.findDangerousVehiclesLive,
        routes.javascript.ReportController.findDangerousVehicles,
        routes.javascript.ReportController.findInformalVehicles,
        routes.javascript.ReportController.findInformalVehiclesLive,
        routes.javascript.ReportController.findRutasInformales,
        routes.javascript.ReportController.findSegmentosCriticos,
        routes.javascript.ReportController.getDataChartVelocity,
        routes.javascript.ReportController.getPasajerosEInformales,
        routes.javascript.RutaController.getRutasAndMetadata

      )

    ).as(MimeTypes.JAVASCRIPT)
  }

}
