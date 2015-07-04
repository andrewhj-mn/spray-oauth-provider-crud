package net.andrewhj.oauth.business.authorization.boundary

import akka.actor.ActorSystem
import akka.pattern.ask
import net.andrewhj.oauth.business.authorization.controller.TokenMessages.{ TokenFailure, TokenRequest }
import net.andrewhj.oauth.business.authorization.controller.{ SlickRelationalAuthorizationCodeRepository, SlickRelationalTokenRepository, TokenMessages }
import net.andrewhj.oauth.business.user.boundary.UserMessages.{ InvalidClient, UserError }
import net.andrewhj.oauth.{ AccessToken, DefaultTimeout, Marshalling }
import spray.http.StatusCodes
import spray.json.{ JsString, JsValue, ProductFormats, RootJsonFormat }
import spray.routing.Directives

trait AccessTokenFormats extends Marshalling with ProductFormats with HashFormats {
  import net.andrewhj.oauth.business.authorization.controller.TokenMessages._
  import net.andrewhj.oauth.core.DateMarshalling._

  implicit val tokenRequestFormat = jsonFormat4(TokenRequest)
  implicit val tokenResponseFormat = jsonFormat3(TokenIssued)
  implicit val accessTokenFormat = jsonFormat5(AccessToken)

  implicit object UserErrorFormat extends RootJsonFormat[UserError] {
    override def read(json: JsValue): UserError = sys.error("Only write is available for failures")

    override def write(obj: UserError): JsValue = obj match {
      case InvalidClient ⇒ JsString("Invalid Client ID")
      case _ ⇒ JsString("Operation failed bro.")
    }
  }

  implicit object TokenFailureFormat extends RootJsonFormat[TokenFailure] {
    override def read(json: JsValue): TokenFailure = sys.error("Only write is available for failures")

    override def write(obj: TokenFailure): JsValue = obj match {
      case IdNotFound ⇒ JsString("Id Not Found")
      case UnmatchedSecret ⇒ JsString("Secret does not match")
      case _ ⇒ JsString("Operation failed bro.")
    }
  }

  //  implicit val unatchedSecretEitherResponseFormat = eitherCustomMarshaller[TokenMessages.UnmatchedSecret, AccessToken](StatusCodes.Unauthorized)
  implicit val tokenEitherResponseFormat = eitherFailureStatusCodeMarshaller[TokenFailure, AccessToken] {
    case IdNotFound ⇒ StatusCodes.Forbidden
    case UnmatchedSecret ⇒ StatusCodes.Unauthorized
  }
}

/**
 * REST endpoint for the issuing of tokens
 */
class AccessTokenResource(implicit val actorSystem: ActorSystem) extends Directives with DefaultTimeout with AccessTokenFormats {
  import scala.concurrent.ExecutionContext.Implicits.global

  val route = path("oauth" / "token") {
    post {
      entity(as[TokenRequest]) { tokenRequest ⇒
        val tokenActor = actorSystem.actorOf(TokenActor.withRepository(SlickRelationalAuthorizationCodeRepository, SlickRelationalTokenRepository))

        complete {
          (tokenActor ? TokenMessages.ExchangeCodeForToken(tokenRequest)).mapTo[Either[TokenFailure, AccessToken]]
        }
      }
    }
  }
}
