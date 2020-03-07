package controllers

import java.util.Calendar

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import javax.inject.Inject
import models.{ DataTravel, Pasajero, Travel }
import models.daos.MobileDAO
import org.joda.time.DateTime
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents }
import shared.LineStringSH
import utils.authlayer.DefaultEnv
import upickle.default._

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Random

/**
 * The basic application controller.
 *
 * @param components  The Play controller components.
 * @param silhouette  The Silhouette stack.
 * @param webJarsUtil The webjar util.
 * @param assets      The Play assets finder.
 */
class TravelController @Inject() (
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

  def registerNewTravel(idRuta: Int, pasajeroID: Int, starTime: Long, placa: String, direction: Boolean, initialPosition: Int) = Action.async { implicit request =>
    val dt = new DateTime(starTime)
    val parameter = for {
      ruta <- mobileDAO.getRuta(idRuta)
      pasajero <- mobileDAO.getPasajero(pasajeroID)
    } yield {
      ruta.isDefined && pasajero.isDefined
    }

    val esInformal = Random.nextInt(10) > (idRuta % 10)
    val travel = Travel(-1, dt, placa, esInformal, idRuta, isLive = true, direction = direction, initialPosition, pasajeroID, infringio = false)
    for {
      correct <- parameter
      res <- (if (correct) {
        mobileDAO.addTravel(travel)
      } else {
        Future.successful(-1)
      })
    } yield {
      Ok(Json.obj("response" -> write(res)))
    }
  }

  def addNewDataTravel(idTravel: Int, position: Int, timeStamp: Long, velocity: Int) = Action.async {
    val dt = new DateTime(timeStamp)
    val dataTravel = DataTravel(-1, dt, idTravel, position, velocity)
    for {
      travel <- mobileDAO.getTravel(idTravel)
      res <- if (travel.isDefined) {
        mobileDAO.addDataTravel(dataTravel)
      } else {
        Future.successful(-1)
      }
    } yield {
      Ok(Json.obj("response" -> write(res)))
    }
  }

}
