package net.andrewhj.oauth.business.authorization.boundary

import java.util.{ Calendar, UUID }

import akka.actor.{ Props, Actor, ActorLogging }
import net.andrewhj.oauth.business.authorization.boundary.AuthorizationActor.{ Forbidden, AuthorizeRequest }
import net.andrewhj.oauth.business.authorization.controller.AuthorizationCodeRepository
import net.andrewhj.oauth.business.authorization.entity.AuthorizationCode
import net.andrewhj.oauth.business.client.controller.ClientRepository
import net.andrewhj.oauth.business.client.entity.Client

import scala.slick.jdbc.JdbcBackend
import scalaz.-\/

object AuthorizationActor {
  case class AuthorizeRequest(clientId: UUID, redirectUri: String, userName: String)

  type AuthorizeValidationRequest = UUID

  sealed trait AuthorizationError
  case object Forbidden extends AuthorizationError
  case class ClientNotFound(clientId: UUID) extends AuthorizationError
  case class TokenNotFound(token: UUID) extends AuthorizationError

  type AuthorizationResponse = Either[AuthorizationError, AuthorizationCode]
  type AuthorizeValidationResponse = Either[AuthorizationError, ClientDetails]

  case class ClientDetails(clientId: UUID)

  def withRepository(clientRepository: ClientRepository, authorizationCodeRepository: AuthorizationCodeRepository) = Props(classOf[AuthorizationActor], clientRepository, authorizationCodeRepository)
}

class AuthorizationActor(clientRepository: ClientRepository, authorizationCodeRepository: AuthorizationCodeRepository) extends Actor with ActorLogging {
  override def receive = {
    case AuthorizeRequest(clientId, redirectUri, userName) ⇒
      log.info(s"Authorizing Request: $clientId, $redirectUri")
      val client: Option[Client] = clientRepository.findOne(clientId)
      val message = client.map { c ⇒ Right(createAuthorizationCodeForClient(c)) }.getOrElse(Left(Forbidden))
      sender ! message

  }

  def createAuthorizationCodeForClient(c: Client): AuthorizationCode = {
    val authorizationCode = UUID.randomUUID()
    val calendar = Calendar.getInstance
    calendar.add(Calendar.MONTH, 1)
    val expiration = calendar.getTime
    val code = AuthorizationCode(authorizationCode, c.id, "", c.redirectUri, expiration, None)
    authorizationCodeRepository.create(code)
    code
  }
}
