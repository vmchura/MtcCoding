package models.mappers

import be.venneborg.refined.{ RefinedMapping, RefinedSupport }
trait CelaRefinedProfile extends slick.jdbc.JdbcProfile
  with RefinedMapping
  with RefinedSupport {

  override val api = new API with RefinedImplicits
}

object CelaRefinedProfile extends CelaRefinedProfile

trait CelaRefinedPostgresProfile extends CelaRefinedProfile with slick.jdbc.PostgresProfile
object CelaRefinedPostgresProfile extends CelaRefinedPostgresProfile
