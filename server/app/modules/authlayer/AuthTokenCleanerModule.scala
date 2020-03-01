package modules.authlayer

import jobs.authlayer.{ AuthTokenCleaner, Scheduler }
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * The job module.
 */
class AuthTokenCleanerModule extends ScalaModule with AkkaGuiceSupport {

  /**
   * Configures the module.
   */
  override def configure() = {
    bindActor[AuthTokenCleaner]("auth-token-cleaner")
    bind[Scheduler].asEagerSingleton()
  }
}
