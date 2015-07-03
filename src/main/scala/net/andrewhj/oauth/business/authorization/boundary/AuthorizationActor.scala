package net.andrewhj.oauth.business.authorization.boundary

import java.util.UUID

import akka.actor.{ Props, Actor, ActorLogging }
import net.andrewhj.oauth.business.authorization.boundary.AuthorizationActor.AuthorizeRequest
import net.andrewhj.oauth.business.authorization.entity.AuthorizationCode
import net.andrewhj.oauth.business.client.controller.ClientRepository

import scala.slick.jdbc.JdbcBackend

object AuthorizationActor {
  case class AuthorizeRequest(clientId: UUID, redirectUri: String)

  //  case class AuthorizeValidationRequest(authorizationCode: UUID)
  type AuthorizeValidationRequest = AuthorizationCode

  sealed trait AuthorizationError
  case object Forbidden extends AuthorizationError
  case class ClientNotFound(clientId: UUID) extends AuthorizationError
  case class TokenNotFound(token: UUID) extends AuthorizationError

  case class AuthorizationCode(token: UUID)

  type AuthorizationResponse = Either[AuthorizationError, AuthorizationCode]
  type AuthorizeValidationResponse = Either[AuthorizationError, ClientDetails]

  case class ClientDetails(clientId: UUID)

  def withRepository(clientRepository: ClientRepository) = Props(classOf[AuthorizationActor], clientRepository)
}

class AuthorizationActor(clientRepository: ClientRepository) extends Actor with ActorLogging {
  override def receive = {
    case AuthorizeRequest(clientId, redirectUri) ⇒ clientRepository.findOne(clientId)
  }
}
