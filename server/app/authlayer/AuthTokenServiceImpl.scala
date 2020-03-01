package authlayer

import java.util.UUID

import com.mohiva.play.silhouette.api.util.Clock
import javax.inject.Inject
import models.authlayer.{ AuthToken, User }
import models.daos.PK
import models.daos.authlayer.AuthTokenDAO
import org.joda.time.DateTimeZone

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.language.postfixOps

class AuthTokenServiceImpl @Inject() (
  authTokenDAO: AuthTokenDAO,
  clock: Clock
)(
  implicit
  ex: ExecutionContext
) extends AuthTokenService {
  /**
   * Creates a new auth token and saves it in the backing store.
   *
   * @param userID The user ID for which the token should be created.
   * @param expiry The duration a token expires.
   * @return The saved auth token.
   */
  override def create(userID: PK[User], expiry: FiniteDuration = 5 minutes): Future[AuthToken] = {
    val token = AuthToken(PK(UUID.randomUUID()), userID, clock.now.withZone(DateTimeZone.UTC).plusSeconds(expiry.toSeconds.toInt))
    authTokenDAO.save(token)
  }

  /**
   * Validates a token ID.
   *
   * @param id The token ID to validate.
   * @return The token if it's valid, None otherwise.
   */
  override def validate(id: PK[AuthToken]): Future[Option[AuthToken]] = authTokenDAO.find(id)

  /**
   * Cleans expired tokens.
   *
   * @return The list of deleted tokens.
   */
  override def clean: Future[Seq[AuthToken]] = authTokenDAO.findExpired(clock.now.withZone(DateTimeZone.UTC)).flatMap { tokens =>
    Future.sequence(tokens.map { token =>
      authTokenDAO.remove(token.id).map(_ => token)
    })

  }
}
