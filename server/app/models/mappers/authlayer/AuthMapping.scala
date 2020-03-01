package models.mappers.authlayer

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.RefinedTypes.EmailType
import models.authlayer.User
import models.daos.PK
import models.mappers.CelaRefinedProfile

trait AuthMapping {
  protected val profile: CelaRefinedProfile
  import profile.api._
  import profile.mapping._

  case class DBUser(
    userID: PK[User],
    firstName: String,
    lastName: String,
    email: EmailType,
    avatarURL: Option[String],
    activated: Boolean
  )

  class Users(tag: Tag) extends Table[DBUser](tag, "usertable") {
    def id = column[PK[User]]("userID", O.PrimaryKey)
    def firstName = column[String]("firstName")
    def lastName = column[String]("lastName")
    def email = column[EmailType]("email")
    def avatarURL = column[Option[String]]("avatarURL")
    def activated = column[Boolean]("activated")
    def * = (id, firstName, lastName, email, avatarURL, activated).mapTo[DBUser]
  }

  case class DBLoginInfo(
    id: UUID,
    providerID: String,
    providerKey: String
  )

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfotable") {
    def id = column[UUID]("id", O.PrimaryKey)
    def providerID = column[String]("providerID")
    def providerKey = column[String]("providerKey")
    def * = (id, providerID, providerKey).mapTo[DBLoginInfo]
  }

  case class DBUserLoginInfo(
    userID: PK[User],
    loginInfoId: UUID
  )

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "userlogininfotable") {
    def userID = column[PK[User]]("userID")
    def loginInfoId = column[UUID]("loginInfoId")
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo(
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: UUID
  )

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfotable") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[UUID]("loginInfoId")
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]

  //query used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo): Query[LoginInfos, DBLoginInfo, Seq] =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)

}
