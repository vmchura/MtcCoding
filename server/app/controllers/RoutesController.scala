package controllers

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import javax.inject.Inject
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
class RoutesController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  authInfoRepository: AuthInfoRepository,
  mailerClient: MailerClient
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def provideRandomLineString = silhouette.UserAwareAction { implicit request =>

    val randomRoute = PeruRoutes.PeruRoutes.randomRoute()
    println(randomRoute.tagName)
    //val ls = LineStringSH((255, 0, 0), 4d, Seq(c0, c1, c2))
    val ls = LineStringSH((255, 0, 0), 4d, randomRoute.coordinates.map { case (a, b, _) => (a, b) }.toIndexedSeq)
    //println("HERE!!!")
    Ok(Json.obj("response" -> write(ls)))

  }

}
