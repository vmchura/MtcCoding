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

/**
 * The basic application controller.
 *
 * @param components  The Play controller components.
 * @param silhouette  The Silhouette stack.
 * @param webJarsUtil The webjar util.
 * @param assets      The Play assets finder.
 */
class PasajeroController @Inject() (
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

  def isRegistered(idPasajero: Int) = Action.async { implicit request =>
    mobileDAO.getPasajero(idPasajero).map(_.isDefined).map { res =>
      Ok(Json.obj("response" -> write(res)))
    }
  }

  def registerNew() = Action.async { implicit request =>
    mobileDAO.addPasajero(Pasajero(-1)).map { res =>
      Ok(Json.obj("response" -> write(res)))
    }
  }

}
