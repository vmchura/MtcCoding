package models.daos
import scala.language.implicitConversions
import java.util.UUID

import slick.lifted.MappedTo

final case class PK[A](value: UUID) extends AnyVal with MappedTo[UUID]
