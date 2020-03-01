package models

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string._

object RefinedTypes {

  type EmailType = String Refined MatchesRegex[W.`"[A-Z0-9._%+-]+@[A-Z0-9.-]+.[A-Z]{2,6}"`.T]

}
