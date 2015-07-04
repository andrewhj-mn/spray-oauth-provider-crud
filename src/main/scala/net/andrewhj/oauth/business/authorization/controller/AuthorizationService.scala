package net.andrewhj.oauth.business.authorization.controller

import java.util.{ Calendar, UUID }

import net.andrewhj.oauth.business.authorization.boundary.AuthorizationActor
import net.andrewhj.oauth.business.authorization.boundary.AuthorizationActor._
import net.andrewhj.oauth.business.authorization.entity.AuthorizationCode
import net.andrewhj.oauth.business.client.controller.ClientRepository

/**
 * For the Authorize Endpoint, which requires the user to authenticate and redirects back to the client with an
 * authorization code (Authorization Code grant type) or access token (Implicit grant type).
 */
class AuthorizationService(clientRepository: ClientRepository, authorizationCodeRepository: AuthorizationCodeRepository) {

  //  import AuthorizeMessages._

  /**
   * Receives a request object for an authorize request, returns a response object with the appropriate response
   */
  // TODO: return a web form (Do you want to authorize?)
  def handleAuthorizeRequest(request: AuthorizeRequest): AuthorizationResponse = {
    // for now, assume client always says yes (as long as you find the client entry)
    val clientId = request.clientId
    clientRepository.findOne(clientId).map(client ⇒ Right(createAuthorizationCode(request)))
      .getOrElse(Left(ClientNotFound(clientId)))
  }

  private def createAuthorizationCode(request: AuthorizeRequest): AuthorizationCode = {
    // create new authorization item
    val uuid = UUID.randomUUID()
    val expiration = Calendar.getInstance
    expiration.add(Calendar.MONTH, 1)
    // save/persist the code
    val authorizationCode = authorizationCodeRepository.create(AuthorizationCode(uuid, request.clientId, "TODO!!",
      request.redirectUri, expiration.getTime, None))
    // send back auth code
    authorizationCode
  }

  /**
   * Receives a request object, returns false if the incoming request is not a valid Authorize Request. If the request
   * is valid, returns an array of retrieved client details together with input. Applications should call this before
   * displaying a login or authorization form to the user
   */
  def validateAuthorizeRequest(request: AuthorizeValidationRequest): AuthorizeValidationResponse =
    authorizationCodeRepository findOne request map { authorizationCode: AuthorizationCode ⇒
      Right(ClientDetails(authorizationCode.clientId))
    } getOrElse Left(TokenNotFound(request))
}