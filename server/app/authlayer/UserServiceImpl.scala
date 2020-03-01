package authlayer

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import javax.inject.Inject
import models.authlayer.User
import models.daos.PK
import models.daos.authlayer.UserDAO

import scala.concurrent.{ ExecutionContext, Future }

class UserServiceImpl @Inject() (userDAO: UserDAO)(implicit ex: ExecutionContext) extends UserService {
  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param id The ID to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  override def retrieve(id: PK[User]): Future[Option[User]] = userDAO.find(id)

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  override def save(user: User): Future[User] = userDAO.save(user)

  /**
   * Saves the social profile for a user.
   *
   * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  override def save(profile: CommonSocialProfile): Future[User] = {
    import eu.timepit.refined.api.RefType
    import eu.timepit.refined.auto._
    import models.RefinedTypes._
    val emailProvied: String = profile.email.map(_.toUpperCase()).getOrElse("undefined@undefined.com")
    val emailRefined: EmailType = RefType.applyRef[EmailType](emailProvied) match {
      case Right(value) => value
      case Left(_) => "UNDEFINED@UNDEFINED.COM"
    }
    userDAO.find(profile.loginInfo).flatMap {
      case Some(user) => userDAO.save(user.copy(
        firstName = profile.firstName.getOrElse("?"),
        lastName = profile.lastName.getOrElse("?"),
        email = emailRefined,
        avatarURL = profile.avatarURL
      ))
      case None => userDAO.save(User(
        PK(UUID.randomUUID()),
        profile.loginInfo,
        profile.firstName.getOrElse("?"),
        profile.lastName.getOrElse("?"),
        emailRefined, profile.avatarURL,
        activated = true))
    }
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)
}
