package net.andrewhj.oauth

import java.util.concurrent.TimeUnit

import akka.actor.Props
import akka.util.Timeout
import net.andrewhj.oauth.business.authorization.boundary.{ AccessTokenResource, AuthorizationResource }
import net.andrewhj.oauth.presentation.login.LoginResource
import spray.routing._

/**
 * Use to configure when application should throw Timeout exception
 */
trait DefaultTimeout {
  implicit val timeout = new Timeout(2, TimeUnit.SECONDS)
}

trait TopLevelRoute extends RouteConcatenation {
  this: BootSystem ⇒

  val routes = new LoginResource().route ~ new AuthorizationResource().route ~ new AccessTokenResource().route

  //    new TodoApi().route ~ new OauthApi().route ~ new HashApi().route ~ new AuthorizeApi().route ~ new TokenApi().route ~ new UserApi().route

  val routeService = actorSystem.actorOf(Props(new ApplicationApiActor(routes)))
}
