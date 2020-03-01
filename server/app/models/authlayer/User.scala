package models.authlayer

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import models.RefinedTypes.EmailType
import models.daos.PK

/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName First name of the authenticated user.
 * @param lastName Last name of the authenticated user.
 * @param email Email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 */
case class User(
  userID: PK[User],
  loginInfo: LoginInfo,
  firstName: String,
  lastName: String,
  email: EmailType,
  avatarURL: Option[String],
  activated: Boolean) extends Identity
