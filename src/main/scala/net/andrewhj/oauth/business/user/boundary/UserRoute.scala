package net.andrewhj.oauth.business.user.boundary

import akka.actor.ActorSystem
import net.andrewhj.oauth.{ DefaultTimeout, Marshalling }
import net.andrewhj.oauth.business.user.entity.User
import spray.http.HttpHeaders.RawHeader
import spray.http.{ StatusCodes, HttpResponse }
import spray.json.ProductFormats
import spray.routing.Directives

trait UserFormats extends Marshalling with ProductFormats {

  import spray.json._

  implicit val userFormat = jsonFormat4(User)
}

/**
 * User Controller.  MOstly a utility for being able to create users.  Disable before publishing
 */
class UserApi(implicit val actorSystem: ActorSystem) extends Directives with DefaultTimeout with UserFormats {

  // TODO: may not need this.  spawn actor and pass message
  //  val oauthActor = actorSystem.actorSelection("/user/application/oauth")
  //  lazy val noCacheHeaders: List[HttpHeader] = List(
  //    HttpHeaders.`Cache-Control`(`no-store`),
  //    RawHeader("""Pragma""", "no-cache")
  //  )

  import scala.concurrent.ExecutionContext.Implicits.global

  val userActor = actorSystem.actorSelection("/user/application/user")

  val locationHeader = { u: User ⇒
    RawHeader("""Location""", s"users/${u.userName}")
  }

  val route = path("users") {
    get {
      complete {
        "The user controller will be here"
      }
    }
  }

  //  val route = {
  //    path("users") {
  //      get {
  //        complete {
  //          (userActor ? AllUsers).mapTo[AllUserResult].map(_.users)
  //        }
  //      } ~ post {
  //        entity(as[User]) { newUserRequest ⇒
  //          val createdUser = (userActor ? CreateUser(newUserRequest)).mapTo[UserCreated].map[HttpResponse] {
  //            //            case UserCreated(u) => HttpResponse(status = StatusCodes.Created, headers = List(locationHeader(u)), entity = HttpEntity(u))
  //            case _ ⇒ HttpResponse(status = StatusCodes.NotAcceptable)
  //          }
  //
  //          complete {
  //            createdUser
  //          }
  //
  //          //          respondWithStatus(StatusCodes.Created) {
  //          //            respondWithHeader(RawHeader("""Location""", s"users/${createdUser.userName}"))
  //          //            complete {
  //          //              createdUser
  //          //            }
  //        }
  //      }
  //    }
  //  }
}

