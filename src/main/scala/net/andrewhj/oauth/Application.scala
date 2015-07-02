package net.andrewhj.oauth

import akka.actor.{ Props, Actor }
import spray.http.StatusCodes
import spray.routing._
import spray.util.LoggingContext

import scala.util.control.NonFatal

/**
 * Http Actor that handles URL calls
 */
class ApplicationApiActor(route: Route) extends HttpServiceActor with CustomErrorHandler {

  override def receive: Receive = runRoute(route)(customExceptionHandler, RejectionHandler.Default, actorRefFactory,
    RoutingSettings.default(actorRefFactory), LoggingContext.fromActorContext(actorRefFactory))
}

/**
 * Custom error handler, If Api call can return OK or One error You can use eitherCustomErrorMarshaller to configure
 * what type of StatusCode should be returned
 */
trait CustomErrorHandler extends Marshalling {

  implicit def customExceptionHandler(implicit log: LoggingContext): ExceptionHandler =
    ExceptionHandler.apply {
      case NonFatal(ErrorResponseException(statusCode, entity)) ⇒
        log.error(s"Application return expected error status code ${statusCode} with entity ${entity} ")
        ctx ⇒ ctx.complete((statusCode, entity))
        case NonFatal(e) ⇒
        log.error(s"Application return unexpected error with exception ${e}")
        ctx ⇒ ctx.complete(StatusCodes.InternalServerError)
    }
}

/**
 * This actor:
 * - when receive Startup message it creates actors that will handle our requests
 * - when receive Shutdown message it stops all actors from context
 */
case class Startup()
case class Shutdown()

class ApplicationActor extends Actor {
  def receive: Receive = {
    case Startup() ⇒ {
      //      context.actorOf(Props[OauthActor], "oauth")
      //      context.actorOf(Props[UserActor], "user")
      sender ! true
    }
    case Shutdown() ⇒ {
      context.children.foreach(ar ⇒ context.stop(ar))
    }
  }
}

