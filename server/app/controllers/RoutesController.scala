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
    /*
    val c0 = (-8486558.521727294, -1111420.8471308986)
    val c1 = (-8610185.27054937, -956184.9863940852)
    val c2 = (-8443008.18975588, -951268.0134295707)

     */
    val c0 = (-75.497897, -11.780516)
    val c1 = (-75.212615, -12.067813)
    val c2 = (-75.566085, -12.633227)

    val ls = LineStringSH((255, 0, 0), 4d, Seq(c0, c1, c2))
    println("HERE!!!")
    Ok(Json.obj("response" -> write(ls)))

  }

}
