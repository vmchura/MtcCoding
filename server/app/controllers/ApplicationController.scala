package controllers

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.mailer.{ Email, MailerClient }
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents }
import utils.authlayer.DefaultEnv
import scala.concurrent.{ ExecutionContext, Future }

/**
 * The basic application controller.
 *
 * @param components  The Play controller components.
 * @param silhouette  The Silhouette stack.
 * @param webJarsUtil The webjar util.
 * @param assets      The Play assets finder.
 */
class ApplicationController @Inject() (
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
  def index = silhouette.UserAwareAction { implicit request =>

    Ok(views.html.home())

  }

  def contactMe = Action { implicit request =>
    request.body.asFormUrlEncoded match {
      case Some(lista) => {
        try {
          val name = lista("name").headOption.getOrElse("NO NAME")
          val email = lista("email").headOption.getOrElse("noemailfound@gmail.com")
          val phone = lista("phone").headOption.getOrElse("NO PHONE")
          val message = lista("message").headOption.getOrElse("NO MESSAGE")
          val send = mailerClient.send(Email(
            subject = "IRI-C " + name + "/" + phone,
            from = email,
            to = Seq("vmchura@gmail.com"),
            bodyText = Some(message),
            bodyHtml = Some(message)
          ))
          println("send")
          Ok(send)

        } catch {
          case e: Throwable => {
            println("ERROR: " + e.toString)
            BadRequest(e.toString)
          }
        }
      }
      case None => BadRequest("")
    }

  }

}
