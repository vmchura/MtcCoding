package models.daos.authlayer

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.mappers.authlayer.AuthMapping

import scala.concurrent.Future

trait PasswordInfoDAO extends AuthMapping {

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]]
  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo]
  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo]
  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo]
  def remove(loginInfo: LoginInfo): Future[Unit]

}
