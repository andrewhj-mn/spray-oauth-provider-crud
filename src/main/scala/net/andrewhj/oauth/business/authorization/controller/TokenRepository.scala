package net.andrewhj.oauth.business.authorization.controller

import java.util.UUID

import net.andrewhj.oauth.helpers.ReadWriteRepository
import net.andrewhj.oauth.{ DatabaseCfg, AccessToken, AccessTokens }

import scala.slick.jdbc.JdbcBackend

trait TokenRepository extends ReadWriteRepository[AccessToken, UUID] {
}

class RelationalTokenRepository(db: JdbcBackend#DatabaseDef, tableQuery: scala.slick.lifted.TableQuery[AccessTokens]) extends TokenRepository {
  import scala.slick.driver.PostgresDriver.simple._

  override def findAll: List[AccessToken] = db withSession { implicit session ⇒
    tableQuery.run.toList
  }

  override def findOne(id: UUID): Option[AccessToken] = db withSession (implicit session ⇒ tableQuery.filter(_.id === id).run.headOption)

  override def update(a: AccessToken): AccessToken = ???

  override def delete(b: UUID): Unit = db withSession { implicit session ⇒
    tableQuery.filter(_.id === b).delete
  }

  override def create(a: AccessToken): AccessToken = db withSession { implicit session ⇒
    (tableQuery returning tableQuery) += a
  }
}

object SlickRelationalTokenRepository extends RelationalTokenRepository(DatabaseCfg.db, DatabaseCfg.accessTokensTable)