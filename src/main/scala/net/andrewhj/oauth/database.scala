package net.andrewhj.oauth

import net.andrewhj.oauth.business.authorization.entity.{ RefreshToken, AuthorizationCode }
import net.andrewhj.oauth.business.client.entity.Client
import net.andrewhj.oauth.business.user.entity.User
import java.sql.Timestamp
import java.util.{ UUID, Date }

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.ProvenShape
import scala.slick.driver.JdbcProfile

trait TimestampMapper {
  implicit val myMapper = MappedColumnType.base[Date, Timestamp](
    d ⇒ new Timestamp(d.getTime), t ⇒ new Date(t.getTime))
}

class Users(tag: Tag) extends Table[User](tag, "oauth_users") {
  def userName = column[String]("user_name", O.NotNull, O.PrimaryKey)

  def password = column[String]("password", O.Nullable)

  def firstName = column[String]("first_name", O.Nullable)

  def lastName = column[String]("last_name", O.Nullable)

  override def * : ProvenShape[User] = (userName, password.?, firstName.?, lastName.?) <> (User.tupled, User.unapply)

}

class Clients(tag: Tag) extends Table[Client](tag, "scala_oauth_clients") {
  def id: Column[UUID] = column[UUID]("id", O.NotNull, O.PrimaryKey)

  def secret: Column[String] = column[String]("secret", O.NotNull)

  def redirectUri: Column[String] = column[String]("redirect_uri", O.NotNull)

  def grantTypes: Column[String] = column[String]("grant_types", O.Nullable)

  def scope: Column[String] = column[String]("scope", O.Nullable)

  def userId: Column[String] = column[String]("user_id", O.Nullable)

  override def * = (id, secret, redirectUri, grantTypes.?, scope.?, userId.?) <> (Client.tupled, Client.unapply)
}

case class AccessToken(token: UUID, clientId: UUID, userId: String, expiration: Date, scope: Option[String] = None)

class AccessTokens(tag: Tag) extends Table[AccessToken](tag, "scala_oauth_access_tokens") with TimestampMapper {

  def id = column[UUID]("access_token", O.NotNull, O.PrimaryKey)

  def clientId = column[UUID]("client_id", O.NotNull)

  def userId = column[String]("user_id", O.NotNull)

  def expires = column[Date]("expires", O.NotNull)

  def scope = column[String]("scope")

  override def * = (id, clientId, userId, expires, scope.?) <> (AccessToken.tupled, AccessToken.unapply)

}

class AuthorizationCodes(tag: Tag) extends Table[AuthorizationCode](tag, "scala_oauth_authorization_codes") with TimestampMapper {
  def authorizationCode = column[UUID]("authorization_code", O.NotNull, O.PrimaryKey)

  def clientId = column[UUID]("client_id", O.NotNull)

  def userId = column[String]("user_id", O.NotNull)

  def redirectUri = column[String]("redirect_uri", O.NotNull)

  def expires = column[Date]("expires", O.NotNull)

  def scope = column[String]("scope", O.Nullable)

  override def * = (authorizationCode, clientId, userId, redirectUri, expires, scope.?) <> (AuthorizationCode.tupled, AuthorizationCode.unapply)
}

class RefreshTokens(tag: Tag) extends Table[RefreshToken](tag, "scala_oauth_refresh_tokens") with TimestampMapper {
  def refreshToken = column[String]("refresh_token", O.NotNull, O.PrimaryKey)

  def clientId = column[String]("client_id", O.NotNull)

  def userId = column[String]("user_id")

  def expires = column[Date]("expires", O.NotNull)

  def scope = column[String]("scope")

  override def * = (refreshToken, clientId, userId.?, expires, scope.?) <> (RefreshToken.tupled, RefreshToken.unapply)

}