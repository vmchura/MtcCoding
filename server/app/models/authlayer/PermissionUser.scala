package models.authlayer

/**
 *   Each User has multiple permissions to achieve actions
 *   this class represents each permission
 */
trait PermissionUser {
  def permissionTag: String
}

object AdminPermission extends PermissionUser {
  override def permissionTag: String = "AdminPermission"
}

