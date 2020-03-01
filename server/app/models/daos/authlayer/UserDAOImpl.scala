package models.daos.authlayer

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.{ Inject, Singleton }
import models.authlayer.User
import models.daos.PK
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.JdbcProfile
import io.scalaland.chimney.dsl._

import models.mappers.CelaRefinedProfile
import scala.concurrent.{ ExecutionContext, Future }

@Singleton()
class UserDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends UserDAO with HasDatabaseConfigProvider[CelaRefinedProfile] with AuthDAO {
  import profile.api._

  implicit class UserDBConvertor(dbUser: DBUser) {
    def toModelUser(loginInfo: LoginInfo): User =
      dbUser.into[User].withFieldConst(_.loginInfo, loginInfo).transform
  }

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  override def find(loginInfo: LoginInfo): Future[Option[User]] = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- slickUsers.filter(_.id === dbUserLoginInfo.userID)
    } yield dbUser

    db.run(userQuery.result.headOption).map {
      _.map { _.toModelUser(loginInfo) }

    }
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  override def find(userID: PK[User]): Future[Option[User]] = {
    val query = for {
      dbUser <- slickUsers.filter(_.id === userID)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
      dbLoginInfo <- slickLoginInfos.filter(_.id === dbUserLoginInfo.loginInfoId)
    } yield (dbUser, dbLoginInfo)

    db.run(query.result.headOption).map {
      _.map {
        case (user, loginInfo) => user.toModelUser(LoginInfo(loginInfo.providerID, loginInfo.providerKey))
      }
    }
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  override def save(user: User): Future[User] = {
    val dbUser = user.into[DBUser].transform
    val dbLoginInfo = DBLoginInfo(UUID.randomUUID(), user.loginInfo.providerID, user.loginInfo.providerKey)
    val loginInfoAction = {
      val retrieveLoginInfo = slickLoginInfos.filter(
        info => info.providerID === user.loginInfo.providerID &&
          info.providerKey === user.loginInfo.providerKey
      ).result.headOption

      val insertLoginInfo = (slickLoginInfos += dbLoginInfo).map(_ => dbLoginInfo)

      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful).getOrElse(insertLoginInfo)
      } yield loginInfo
    }

    val actions = (
      for {
        _ <- slickUsers.insertOrUpdate(dbUser)
        loginInfo <- loginInfoAction
        _ <- slickUserLoginInfos += DBUserLoginInfo(dbUser.userID, loginInfo.id)
      } yield ()
    ).transactionally
    db.run(actions).map(_ => user)
  }

}
