package net.andrewhj.oauth.business.user.boundary

import akka.actor.{ Props, Actor, ActorLogging }
import net.andrewhj.oauth.business.user.controller.{ UserService, Slick$UserRepository, UserRepository, RelationalUserRepository }

import net.andrewhj.oauth.business.user.entity.User
import net.andrewhj.oauth.DatabaseCfg._

import scalaz.Reader

object UserMessages {

  sealed trait Command

  case class CreateUser(user: User) extends Command

  sealed trait Query

  case class FindUser(userName: String) extends Query

  case object AllUsers extends Query

  sealed trait QueryResult

  case class FoundUser(user: User) extends QueryResult

  case class AllUserResult(users: List[User]) extends QueryResult

  case class NotFound(userName: String) extends QueryResult

  sealed trait Event

  case class UserCreated(user: User)

  sealed trait UserError

  case object InvalidClient extends UserError

}

object UserActor {
  def withRepository(userRepository: UserRepository) = Props(classOf[UserActor], userRepository)
}

class UserActor(userRepository: UserRepository) extends Actor with ActorLogging with UserService {
  this: UserService ⇒

  import net.andrewhj.oauth.business.user.boundary.UserMessages._

  override def receive: Actor.Receive = {
    case AllUsers ⇒
      sender ! AllUserResult(userRepository findAll)
    case CreateUser(u) ⇒ {
      val userResult = userRepository.create(u)
      sender ! UserCreated(userResult)
    }
    case FindUser(name) ⇒ {
      val user = userRepository.findOne(name)
      sender ! user
    }
  }

  def run[R](reader: Reader[UserRepository, R]) = {
    reader(userRepository)
  }
}