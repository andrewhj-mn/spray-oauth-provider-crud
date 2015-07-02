package net.andrewhj.oauth.business.client.controller

import java.util.UUID

import net.andrewhj.oauth.{ Clients, Users }
import net.andrewhj.oauth.business.client.entity.Client
import net.andrewhj.oauth.business.user.entity.User
import net.andrewhj.oauth.helpers.CrudRepository

import scala.slick.jdbc.JdbcBackend

/**
 * A repository for working on Clients
 */
trait ClientRepository extends CrudRepository[Client, UUID] {
}

abstract class RelationalClientRepository(db: JdbcBackend#DatabaseDef) extends ClientRepository {

  import scala.slick.driver.PostgresDriver.simple._

  val tableQuery = TableQuery[Clients]

  override def create(a: Client): Client = db withSession { implicit session ⇒
    (tableQuery returning tableQuery) += a
  }

  override def update(a: Client): Client = ???

  override def delete(b: UUID): Unit = ???

  override def findOne(id: UUID): Option[Client] = ???

  override def findAll: List[Client] = db withSession { implicit session ⇒
    val allUsers = tableQuery.run.toList
    allUsers
  }
}
