package controllers

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import javax.inject.Inject
import models.Pasajero
import models.daos.MobileDAO
import models.mappers.RouteMobileMapping
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents }
import shared.{ DataTravelSH, LineStringSH, TravelSH }
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
class ReportController @Inject() (
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

  def findDangerousVehiclesLive(idRuta: Int) = Action.async { implicit request =>
    mobileDAO.findDangerousVehiclesLive(idRuta).map { s =>
      val toSend: Seq[(TravelSH, DataTravelSH)] = s.map { case (travel, dataTravel) => (travel.toTravelSH(), dataTravel.toDataTravelSH(idRuta)) }
      Ok(Json.obj("response" -> write(toSend)))
    }
  }
  def findDangerousVehicles(idRuta: Int) = Action.async { implicit request =>
    mobileDAO.findDangerousVehicles(idRuta).map { s =>
      val toSend: Seq[TravelSH] = s.map(_.toTravelSH())
      Ok(Json.obj("response" -> write(toSend)))
    }

  }

  def findInformalVehicles(idRuta: Int) = Action.async { implicit request =>
    mobileDAO.findInformalVehicles(idRuta).map { s =>
      val toSend: Seq[TravelSH] = s.map(_.toTravelSH())
      Ok(Json.obj("response" -> write(toSend)))
    }

  }

  def findInformalVehiclesLive(idRuta: Int) = Action.async { implicit request =>
    mobileDAO.findInformalVehiclesLive(idRuta).map { s =>
      val toSend: Seq[(TravelSH, DataTravelSH)] = s.map { case (travel, dataTravel) => (travel.toTravelSH(), dataTravel.toDataTravelSH(idRuta)) }
      Ok(Json.obj("response" -> write(toSend)))
    }
  }

  def findRutasInformales() = Action.async { implicit request =>
    mobileDAO.findRutasInformales().map { s =>
      Ok(Json.obj("response" -> write(s)))
    }

  }

  def findSegmentosCriticos(rutaID: Int) = Action.async { implicit request =>
    mobileDAO.findSegmentosCriticos(rutaID).map { s =>
      Ok(Json.obj("response" -> write(s)))

    }
  }

  def getDataChartVelocity(idRuta: Int) = Action.async { implicit request =>
    mobileDAO.getDataChartVelocity(idRuta).map { s =>
      Ok(Json.obj("response" -> write(s)))

    }
  }

  def getPasajerosEInformales(idRuta: Int) = Action.async { implicit request =>
    mobileDAO.getPasajerosEInformales(idRuta).map { p =>
      Ok(Json.obj("response" -> write(p)))
    }

  }
  def getSegmentosVelocidad(rutaID: Int) = Action.async { implicit request =>
    mobileDAO.getSegmentosVelocidad(rutaID).map { p =>

      Ok(Json.obj("response" -> write(p)))
    }

  }

}
