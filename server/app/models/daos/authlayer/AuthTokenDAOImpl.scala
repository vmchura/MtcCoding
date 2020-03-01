package models.daos.authlayer

import javax.inject.{ Inject, Singleton }
import models.authlayer.AuthToken
import models.daos.{ CustomColumnTypes, PK }
import models.mappers.CelaRefinedProfile
import models.mappers.authlayer.AuthTokenMapping
import org.joda.time.DateTime
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Give access to the [[models.authlayer.AuthToken]] object.
 */
@Singleton()
class AuthTokenDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider,
  protected val customColumnTypes: CustomColumnTypes)(implicit executionContext: ExecutionContext)
  extends AuthTokenDAO with AuthTokenMapping with HasDatabaseConfigProvider[CelaRefinedProfile] {

  import profile.api._

  import customColumnTypes.jodaDateTimeType
  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  override def find(id: PK[AuthToken]): Future[Option[AuthToken]] =
    db.run(slickAuthTokens.filter(_.id === id).result.headOption)

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  override def findExpired(dateTime: DateTime): Future[Seq[AuthToken]] =
    db.run(slickAuthTokens.filter(_.expiry <= dateTime).result)

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  override def save(token: AuthToken): Future[AuthToken] =
    db.run(slickAuthTokens += token).map(_ => token)

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def remove(id: PK[AuthToken]): Future[Unit] =
    db.run(slickAuthTokens.filter(_.id === id).delete).map(_ => ())

}