package models.daos

import java.sql.Timestamp

import javax.inject.{ Inject, Singleton }
import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.JdbcProfile

@Singleton()
class CustomColumnTypes @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  implicit val jodaDateTimeType =
    MappedColumnType.base[DateTime, Timestamp](
      dt => new Timestamp(dt.getMillis),
      ts => new DateTime(ts.getTime, UTC)
    )

}

