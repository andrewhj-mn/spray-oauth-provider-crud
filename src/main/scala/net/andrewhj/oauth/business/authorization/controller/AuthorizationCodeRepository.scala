package net.andrewhj.oauth.business.authorization.controller

import java.util.UUID

import net.andrewhj.oauth.{ AuthorizationCodes, DatabaseCfg }
import net.andrewhj.oauth.business.authorization.entity.AuthorizationCode
import net.andrewhj.oauth.helpers.ReadWriteRepository

import scala.slick.jdbc.JdbcBackend

trait AuthorizationCodeRepository extends ReadWriteRepository[AuthorizationCode, UUID]
/**
 * Repository for persisting and looking up authorization codes
 */
abstract class RelationalAuthorizationCodeRepository(db: JdbcBackend#DatabaseDef, tableQuery: scala.slick.lifted.TableQuery[AuthorizationCodes]) extends AuthorizationCodeRepository {
  import scala.slick.driver.PostgresDriver.simple._

  override def create(a: AuthorizationCode): AuthorizationCode = db withSession { implicit session ⇒
    (tableQuery returning tableQuery) += a
  }

  override def update(a: AuthorizationCode): AuthorizationCode = ???

  override def delete(b: UUID): Unit = db withSession { implicit session ⇒
    tableQuery.filter(_.authorizationCode === b).delete
  }

  override def findOne(id: UUID): Option[AuthorizationCode] = db withSession { implicit session ⇒
    tableQuery.filter(_.authorizationCode === id).run.headOption
  }

  //  def findByCodeAndSecret(code: UUID, secret: String): Option[AuthorizationCode] = db withSession { implicit session =>
  //    tableQuery.filter(x => x.authorizationCode === id && x.cli)
  //  }

  override def findAll: List[AuthorizationCode] = db withSession { implicit session ⇒
    tableQuery.run.toList
  }
}

object SlickRelationalAuthorizationCodeRepository extends RelationalAuthorizationCodeRepository(DatabaseCfg.db, DatabaseCfg.authorizationCodesTable)
