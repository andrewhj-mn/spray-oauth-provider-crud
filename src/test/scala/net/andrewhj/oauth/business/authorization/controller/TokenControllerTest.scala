package net.andrewhj.oauth.business.authorization.controller

import java.util.{ Calendar, UUID }

import net.andrewhj.oauth.AccessToken
import net.andrewhj.oauth.business.ControllerSuite
import net.andrewhj.oauth.business.authorization.controller.TokenMessages.TokenRequest
import net.andrewhj.oauth.business.authorization.entity.AuthorizationCode

class TokenControllerTest extends ControllerSuite {
  trait TokenControllerDeps {
    val clientId = UUID.fromString("51ce5c1c-2089-4dbc-a962-7cb30fec14f7")
    val authCode = UUID.fromString("b33a8e84-937f-464c-ad8a-0f4d549a3f6e")
    val tokenRequest: TokenRequest = TokenRequest(clientId, "secret", authCode, "localhost")

    val calendar = Calendar.getInstance
    calendar.add(Calendar.MONTH, 1)
    val authCodeExpires = calendar.getTime
    val userId: String = "TBD!!!"
    val redirectUri: String = "localhost"

    val authorizationCodeRepository: AuthorizationCodeRepository = stub[AuthorizationCodeRepository]
    val tokenRepository: TokenRepository = stub[TokenRepository]
    val tokenController = new TokenController(authorizationCodeRepository, tokenRepository)
  }

  "A token controller" when {
    "authorization code is invalid" should {
      "reject request" in new TokenControllerDeps {
        (authorizationCodeRepository findOne _).when(authCode).returns(None)
        assert(tokenController.convertTokenRequest(tokenRequest).isLeft)
      }

    }

    "authorization code is valid" should {
      trait ValidTokenBehaviors extends TokenControllerDeps {

      }
      "generate token" in new TokenControllerDeps {
        calendar.add(Calendar.MONTH, 1)

        val tokenResponse = AuthorizationCode(authCode, clientId, userId, redirectUri, authCodeExpires, None)
        (authorizationCodeRepository findOne _).when(authCode).returns(Some(tokenResponse))
        private val accessToken: AccessToken = AccessToken(UUID.randomUUID(), clientId, userId, calendar.getTime, None)
        (tokenRepository create _).when(*).returns(accessToken).once()
        assert(tokenController.convertTokenRequest(tokenRequest).isRight)
      }
      "remove auth code when token is generated" in new TokenControllerDeps {
        calendar.add(Calendar.MONTH, 1)

        val tokenResponse = AuthorizationCode(authCode, clientId, userId, redirectUri, authCodeExpires, None)
        (authorizationCodeRepository findOne _).when(authCode).returns(Some(tokenResponse))
        private val accessToken: AccessToken = AccessToken(UUID.randomUUID(), clientId, userId, calendar.getTime, None)
        (tokenRepository create _).when(*).returns(accessToken).once()
        (authorizationCodeRepository delete _).when(authCode).once()
        tokenController.convertTokenRequest(tokenRequest)
      }

    }

  }
}
