package models.mappers.authlayer

import models.authlayer.{ AuthToken, User }
import models.daos.{ CustomColumnTypes, PK }
import org.joda.time.DateTime
import slick.jdbc.JdbcProfile

trait AuthTokenMapping {
  protected val profile: JdbcProfile
  protected val customColumnTypes: CustomColumnTypes
  import profile.api._
  import customColumnTypes.jodaDateTimeType

  class AuthTokenTable(tag: Tag) extends Table[AuthToken](tag, "authtokentable") {
    def id = column[PK[AuthToken]]("id", O.PrimaryKey)
    def userID = column[PK[User]]("userID")
    def expiry = column[DateTime]("expiry")

    def * = (id, userID, expiry).mapTo[AuthToken]
  }

  val slickAuthTokens = TableQuery[AuthTokenTable]
}
