package net.andrewhj.oauth

import scala.slick.jdbc.JdbcBackend
import scala.slick.jdbc.meta.MTable
import java.util.UUID
import scala.slick.driver.PostgresDriver
import scala.slick.jdbc.meta.MTable
import scala.slick.profile.SqlProfile
import scala.util.Properties

object DatabaseCfg {

  import scala.slick.driver.PostgresDriver.simple._
  import scala.slick.profile.SqlProfile

  val db = Database.forURL("jdbc:postgresql://192.168.99.100:32768/oauth_provider_crud", user = "oauth", password = "oauth")

  val clientsTable: TableQuery[Clients] = TableQuery[Clients]
  val accessTokensTable = TableQuery[AccessTokens]
  val authorizationCodesTable = TableQuery[AuthorizationCodes]
  val refreshTokensTable = TableQuery[RefreshTokens]
  val usersTable = TableQuery[Users]

  //  val scopesTable = TableQuery[Scopes]
  //  val jwtsTable = TableQuery[Jwts]

  lazy val tables = List(clientsTable, accessTokensTable, authorizationCodesTable, refreshTokensTable, usersTable) //, scopesTable, jwtsTable)
  lazy val tableDdl = clientsTable.ddl ++ accessTokensTable.ddl ++ authorizationCodesTable.ddl ++ refreshTokensTable.ddl ++ usersTable.ddl //++ scopesTable.ddl ++ jwtsTable.ddl

  def blash() = {
    db.withSession(implicit session ⇒
      usersTable.run.toList
    )
  }

  def init() = {
    db.withTransaction { implicit session ⇒
      if (MTable.getTables("scala_oauth_clients").list.isEmpty) {
        tables foreach (t ⇒ t.ddl.create)
      }
    }
  }

  def create() = {
    db withTransaction (implicit session ⇒ tables foreach (_.ddl.create))
  }

  def drop() = {
    db withTransaction { implicit session ⇒
      tables foreach (t ⇒ t.ddl.drop)
    }
  }

  def createDdl = {
    tableDdl.createStatements

  }

  def dropDdl = {
    tableDdl.dropStatements
  }
}