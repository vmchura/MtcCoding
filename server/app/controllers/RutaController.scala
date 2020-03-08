package controllers

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import javax.inject.Inject
import models.Pasajero
import models.daos.MobileDAO
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents }
import shared.LineStringSH
import utils.authlayer.DefaultEnv
import upickle.default._

import scala.concurrent.{ ExecutionContext, Future }
import PeruRoutes.PeruRoutes.rutasOnMemory

/**
 * The basic application controller.
 *
 * @param components  The Play controller components.
 * @param silhouette  The Silhouette stack.
 * @param webJarsUtil The webjar util.
 * @param assets      The Play assets finder.
 */
class RutaController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  authInfoRepository: AuthInfoRepository,
  mailerClient: MailerClient,
  mobileDAO: MobileDAO

)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  def getRouteData(idRoute: Int) = Action.async { implicit request =>
    mobileDAO.getRuta(idRoute).map {
      case Some(ruta) => Ok(Json.obj("idRute" -> ruta.idRuta, "tagRuta" -> ruta.tagRuta, "longRuta" -> ruta.progFin))
      case None => BadRequest(Json.obj("response" -> "not route found"))
    }
  }

  def getRutasID() = Action.async { implicit request =>
    mobileDAO.getAllRutas().map { rutas =>
      Ok(Json.obj("ids" -> write(rutas.map(_.idRuta).mkString("/"))))
    }
  }

  def getRutasAndMetadata() = Action.async {
    mobileDAO.getAllRutasSHMeta().map { seq =>
      Ok(Json.obj("response" -> write(seq)))
    }
  }
  def getLineStringRuta(idRuta: Int) = Action {
    if (rutasOnMemory.contains(idRuta)) {
      val ls = LineStringSH((0, 0, 255), 4d, rutasOnMemory(idRuta).coordinates.map { case (a, b, _) => (a, b) }.toIndexedSeq)
      Ok(Json.obj("response" -> write(ls)))
    } else {
      BadRequest("ERROR, RUTA NO EXISTE")
    }
  }
}
