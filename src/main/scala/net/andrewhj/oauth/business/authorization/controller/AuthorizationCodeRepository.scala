package net.andrewhj.oauth.business.authorization.controller

import java.util.UUID

import net.andrewhj.oauth.business.authorization.entity.AuthorizationCode
import net.andrewhj.oauth.helpers.ReadWriteRepository

/**
 * Repository for persisting and looking up authorization codes
 */
abstract class AuthorizationCodeRepository extends ReadWriteRepository[AuthorizationCode, UUID] {
  override def create(a: AuthorizationCode): AuthorizationCode = ???

  override def update(a: AuthorizationCode): AuthorizationCode = ???

  override def delete(b: UUID): Unit = ???

  override def findOne(id: UUID): Option[AuthorizationCode] = ???

  override def findAll: List[AuthorizationCode] = ???
}
