package net.andrewhj.oauth.presentation.login

import akka.actor.ActorSystem
import net.andrewhj.oauth.DefaultTimeout
import net.andrewhj.oauth.business.user.boundary.UserActor
import net.andrewhj.oauth.business.user.entity.User
import spray.http._
import spray.http.HttpHeaders.`Content-Type`
import spray.routing.Directives

/**
 * Login routes for directly addressing the server (the entrypoint for /)
 */
class LoginResource(implicit val actorSystem: ActorSystem) extends Directives with DefaultTimeout {
  import scala.concurrent.ExecutionContext.Implicits.global

  val route = {
    path("") {
      get {
        respondWithMediaType(MediaTypes.`text/html`) {
          complete {
            """
              |<html>
              |<head>
              |<title>Login to view the page</title>
              |</head>
              |<body>
              |<p>Login page will go here</p>
              |<form method="POST">
              |<input type="text" id="userName" placeholder="Username" />
              |<input type="text" id="password" placeholder="Password" />
              |<input type="submit" value="Login" />
              |</form>
              |</body>
              |</html>
            """.stripMargin
          }
        }
      } ~
        post {
          val (status, message) = lookupUser("test").map(u ⇒ (StatusCodes.OK, "User logged in"))
            .getOrElse((StatusCodes.Unauthorized, "Bad user or password"))
          respondWithStatus(status)
          complete {
            message
          }
        }
    }
  }

  def lookupUser(userName: String)(implicit system: ActorSystem): Option[User] = {
    // TODO: figure out how to inject the userRepository Dependency (or can this be done at a higher level?
    //    val userActor = system.actorOf(UserActor.props)
    None
  }

}
