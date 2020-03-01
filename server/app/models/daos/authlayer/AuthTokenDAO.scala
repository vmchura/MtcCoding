package models.daos.authlayer

import models.authlayer.AuthToken
import models.daos.PK
import org.joda.time.DateTime

import scala.concurrent.Future
/**
 * Give access to the [[models.authlayer.AuthToken]] object.
 */
trait AuthTokenDAO {

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  def find(id: PK[AuthToken]): Future[Option[AuthToken]]

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  def findExpired(dateTime: DateTime): Future[Seq[AuthToken]]

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  def save(token: AuthToken): Future[AuthToken]

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(id: PK[AuthToken]): Future[Unit]
}