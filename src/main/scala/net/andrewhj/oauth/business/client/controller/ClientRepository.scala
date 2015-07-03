package net.andrewhj.oauth.business.client.controller

import java.util.UUID

import net.andrewhj.oauth.{ Clients, Users }
import net.andrewhj.oauth.business.client.entity.Client
import net.andrewhj.oauth.business.user.entity.User
import net.andrewhj.oauth.helpers.CrudRepository

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted
import scala.slick.lifted.AbstractTable

/**
 * A repository for working on Clients
 */
trait ClientRepository extends CrudRepository[Client, UUID] {
}

//trait RelationalRepository[I, IID, TBL <: AbstractTable[TBL]] extends CrudRepository[I, IID] {
//  import scala.slick.driver.PostgresDriver.simple._
//
//  def db: JdbcBackend#DatabaseDef
//  def tableQuery: scala.slick.lifted.TableQuery[TBL]
//  //  def tableQuery: TableQuery[Clients]
//
//  private val query: lifted.TableQuery[TBL] = TableQuery[TBL]
//
//  override def create(a: I): I = ???
//
//  override def update(a: I): I = ???
//
//  override def delete(b: IID): Unit = ???
//
//  override def findOne(id: IID): Option[I] = ???
//
//  override def findAll: List[I] = ???
//  //  override def findAll: List[I] = db withSession { implicit session =>
//  //
//  //  }
//}

//abstract class RelationalClientRepository(db: JdbcBackend#DatabaseDef) extends RelationalRepository[Client, UUID, Clients] {
//abstract class RelationalClientRepository(db: JdbcBackend#DatabaseDef) extends ClientRepository {
abstract class RelationalClientRepository(db: JdbcBackend#DatabaseDef, tableQuery: scala.slick.lifted.TableQuery[Clients]) extends ClientRepository {

  import scala.slick.driver.PostgresDriver.simple._

  //  val tableQuery = TableQuery[Clients]

  override def create(a: Client): Client = db withSession { implicit session ⇒
    (tableQuery returning tableQuery) += a
  }

  override def update(a: Client): Client = ???

  override def delete(b: UUID): Unit = db withSession { implicit session ⇒
    tableQuery.filter(_.id === b).delete
  }

  override def findOne(id: UUID): Option[Client] = db withSession { implicit session ⇒
    tableQuery.filter(_.id === id).run.headOption
  }

  override def findAll: List[Client] = db withSession { implicit session ⇒
    val allUsers = tableQuery.run.toList
    allUsers
  }
}
