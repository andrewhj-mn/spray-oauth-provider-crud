package net.andrewhj.oauth.business.authorization.boundary

import akka.actor.{ Props, Actor, ActorLogging }
import net.andrewhj.oauth.business.authorization.boundary.AuthorizationActor.AuthorizationError
import net.andrewhj.oauth.business.authorization.controller.{ TokenController, TokenRepository, AuthorizationCodeRepository }
import net.andrewhj.oauth.business.authorization.controller.TokenMessages.{ TokenIssued, IdNotFound, ExchangeCodeForToken }
import net.andrewhj.oauth.business.authorization.entity.AuthorizationCode

object TokenActor {
  def withRepository(authorizationCodeRepository: AuthorizationCodeRepository, tokenRepository: TokenRepository) =
    Props(classOf[TokenActor], new TokenController(authorizationCodeRepository, tokenRepository))
}

/**
 * Handles the issuing and verification of tokens.
 */
class TokenActor(tokenController: TokenController) extends Actor with ActorLogging {
  override def receive = {
    case ExchangeCodeForToken(x) ⇒
      val tokenOrError: Either[AuthorizationError, TokenIssued] = tokenController.convertTokenRequest(x)
      log.debug(s"Token back: $tokenOrError")
      sender ! tokenOrError
  }

}
