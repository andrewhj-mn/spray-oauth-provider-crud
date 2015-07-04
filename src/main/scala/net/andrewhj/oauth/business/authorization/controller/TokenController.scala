package net.andrewhj.oauth.business.authorization.controller

import java.util.{ Calendar, Date, UUID }

import net.andrewhj.oauth.{ AccessToken, DatabaseCfg }
import net.andrewhj.oauth.business.authorization.boundary.AuthorizationActor.{ TokenNotFound, AuthorizationError }
import net.andrewhj.oauth.business.authorization.controller.TokenMessages.{ TokenResponse, TokenRequest }
import net.andrewhj.oauth.business.authorization.entity.AuthorizationCode

object TokenMessages {
  case class TokenRequest(clientId: UUID, secret: String, code: UUID, redirectUri: String)
  //case class TokenRequest(grantType: String, clientId: UUID, secret: String, code: String, redirectUri: String)

  case class TokenResponse(accessToken: String, expires: Date, tokenType: String)
}

/**
 * Exchanages AuthorizationCode for Token
 */
class TokenController(authorizationCodeRepository: AuthorizationCodeRepository, tokenRepository: TokenRepository) {
  def convertTokenRequest(request: TokenRequest): Either[AuthorizationError, TokenResponse] = {
    val blah = UUID.randomUUID()
    authorizationCodeRepository.findOne(request.code).map(authCode ⇒ Right(exchangeCodeForToken(authCode)))
      .getOrElse(Left(TokenNotFound(request.code)))
  }

  def exchangeCodeForToken(authCode: AuthorizationCode): TokenResponse = {
    val token = UUID.randomUUID()
    val calendar = Calendar.getInstance
    calendar.add(Calendar.MONTH, 2)
    val expires = calendar.getTime
    val accessToken: AccessToken = AccessToken(token, authCode.clientId, authCode.userId, expires, None)
    val createdToken: AccessToken = tokenRepository.create(accessToken)
    authorizationCodeRepository.delete(authCode.authorizationCode)
    TokenResponse(createdToken.token.toString, createdToken.expiration, "bearer")
  }
}
