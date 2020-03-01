package models.daos.authlayer

import models.mappers.CelaRefinedProfile
import models.mappers.authlayer.AuthMapping
import play.api.db.slick.HasDatabaseConfigProvider

trait AuthDAO extends AuthMapping with HasDatabaseConfigProvider[CelaRefinedProfile]