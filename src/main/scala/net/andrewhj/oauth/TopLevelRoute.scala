package net.andrewhj.oauth

import java.util.concurrent.TimeUnit

import akka.actor.Props
import akka.util.Timeout
import spray.routing._

/**
 * Use to configure when application should throw Timeout exception
 */
trait DefaultTimeout {
  implicit val timeout = new Timeout(2, TimeUnit.SECONDS)
}

trait TopLevelRoute extends RouteConcatenation {
  this: BootSystem ⇒

  val routes = new HelloRoute().route

  //    new TodoApi().route ~ new OauthApi().route ~ new HashApi().route ~ new AuthorizeApi().route ~ new TokenApi().route ~ new UserApi().route

  val routeService = actorSystem.actorOf(Props(new ApplicationApiActor(routes)))
}
