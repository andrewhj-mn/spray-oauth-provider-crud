package net.andrewhj.oauth.business.authorization.boundary

import java.util.UUID

import akka.actor.ActorSystem
import akka.pattern.ask
import net.andrewhj.oauth.business.authorization.boundary.AuthorizationActor.{ AuthorizationResponse, AuthorizeRequest }
import net.andrewhj.oauth.business.authorization.controller.SlickRelationalAuthorizationCodeRepository
import net.andrewhj.oauth.business.client.controller.SlickRelationalClientRepository
import net.andrewhj.oauth.{ DefaultTimeout, Marshalling }
import spray.http.StatusCodes
import spray.json.ProductFormats
import spray.routing.Directives

import scala.concurrent.Future
import scala.util.{ Failure, Success }

trait HashFormats extends Marshalling with ProductFormats {

  import spray.json._

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    override def write(x: UUID): JsValue = JsString(x.toString)

    override def read(value: JsValue): UUID = value match {
      case JsString(x) ⇒ UUID.fromString(x)
      case _ ⇒ deserializationError("expecting JsString")

    }
  }

}

trait AuthorizationFormats extends Marshalling with ProductFormats with HashFormats {
}

/**
 * REST resource for the Authorization action of OAuth.
 */
class AuthorizationResource(implicit val actorSystem: ActorSystem) extends Directives with DefaultTimeout {
  import scala.concurrent.ExecutionContext.Implicits.global

  val route = {
    path("oauth" / "authorize") {
      /**
       * The user is directed here by the client in order to authorize the client app
       * to access his/her data
       */
      get {
        // TODO: the redirect is working.  Now we need to validate, generate a token, and pass that result back into the response

        parameters('response_type ! "code", 'client_id, 'redirect_uri) { (clientId, redirectUri) ⇒
          // TODO: we need the user context so we can associate them with the authorization code
          val userId = "tom"

          val clientUuid = UUID.fromString(clientId)
          val actor = actorSystem.actorOf(AuthorizationActor.withRepository(SlickRelationalClientRepository, SlickRelationalAuthorizationCodeRepository))

          val future: Future[AuthorizationResponse] = (actor ? AuthorizeRequest(clientUuid, redirectUri, userId)).mapTo[AuthorizationResponse]

          onComplete(future) {
            case Failure(t: Throwable) ⇒ respondWithStatus(StatusCodes.InternalServerError) {
              complete(t)
            }
            case Success(x) ⇒ x match {

              case Left(_) ⇒ respondWithStatus(StatusCodes.Forbidden) {
                complete {
                  ""
                }
              }
              case Right(authCode) ⇒ redirect(s"$redirectUri?token=${authCode.authorizationCode}", StatusCodes.Found)
            }
          }
        }
      }
    }
  }

}
