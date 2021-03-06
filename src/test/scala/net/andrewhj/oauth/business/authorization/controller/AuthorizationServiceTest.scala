package net.andrewhj.oauth.business.authorization.controller

import java.util.{ Calendar, UUID }

import net.andrewhj.oauth.business.ControllerSuite
import net.andrewhj.oauth.business.authorization.boundary.AuthorizationActor
import net.andrewhj.oauth.business.authorization.boundary.AuthorizationActor._
import net.andrewhj.oauth.business.authorization.entity.AuthorizationCode
import net.andrewhj.oauth.business.client.controller.ClientRepository
import net.andrewhj.oauth.business.client.entity.Client
import net.andrewhj.oauth.business.user.entity.User

class AuthorizationServiceTest extends ControllerSuite {
  trait CredentialBuilder {
    val user = User("test", Some("testpass"))
    val mockClientRepository = stub[ClientRepository]
    val mockAuthorizationCodeRepository = mock[AuthorizationCodeRepository]
    val authorizeController = new AuthorizationService(mockClientRepository, mockAuthorizationCodeRepository)
    val clientId = UUID.fromString("83baa3f2-7640-44ff-bbac-43f07156f91d")
    val authorizeRequest = AuthorizeRequest(clientId, "localhost", user.userName)

  }

  trait ValidCredentialBuilder extends CredentialBuilder {
    val client = Client(clientId, "secret", "localhost")
    (mockClientRepository findOne _).when(clientId).returns(Some(client))
    val authCode: UUID = UUID.randomUUID()
    val calendar = Calendar.getInstance
    calendar.add(Calendar.MONTH, 1)
    val expiration = calendar.getTime

    val authorizationCode: AuthorizationCode = AuthorizationCode(authCode, clientId, user.userName,
      "localhost", expiration, None)
  }
  trait UncheckedCreate extends ValidCredentialBuilder {
    mockAuthorizationCodeRepository.create _ expects * returning authorizationCode anyNumberOfTimes ()
  }

  trait RequiredCreate extends ValidCredentialBuilder {
    mockAuthorizationCodeRepository.create _ expects * returning authorizationCode
  }

  "Authorization controller" when {
    "handling authorization requests" when {
      "credentials are invalid" should {
        "return error" in new CredentialBuilder {
          (mockClientRepository findOne _).when(clientId).returns(None)
          assert(authorizeController.handleAuthorizeRequest(authorizeRequest) === Left(ClientNotFound(clientId)))
        }
      }

      "credentials are valid" should {
        "return new authorization token" in new UncheckedCreate {
          val response: AuthorizationResponse = authorizeController.handleAuthorizeRequest(authorizeRequest)
          assert(response.isRight)
        }

        "save authorization token" in new RequiredCreate {
          authorizeController.handleAuthorizeRequest(authorizeRequest)
        }

      }
    }
    "validating authorization requests" when {
      trait TokenBuilder extends ValidCredentialBuilder {

      }
      "credentials are invalid" should {
        "return error" in new TokenBuilder {
          (mockAuthorizationCodeRepository findOne _).expects(authCode).returning(None) anyNumberOfTimes ()
          val code: AuthorizationActor.AuthorizeValidationRequest = authCode
          assert(authorizeController.validateAuthorizeRequest(code).isLeft)
        }
        "return client information" in new TokenBuilder {
          (mockAuthorizationCodeRepository findOne _).expects(authCode).returning(Some(authorizationCode))
          val code: AuthorizationActor.AuthorizeValidationRequest = authCode
          assert(authorizeController.validateAuthorizeRequest(code).isRight)
        }
      }
    }
  }
}
