package modules.authlayer

import authlayer.{ AuthTokenService, AuthTokenServiceImpl }
import com.google.inject.AbstractModule
import models.daos.authlayer.{ AuthTokenDAO, AuthTokenDAOImpl }
import net.codingwell.scalaguice.ScalaModule

class AuthTokenModule() extends AbstractModule with ScalaModule {
  /**
   * Configures the module.
   */
  override def configure(): Unit = {
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]
  }
}
