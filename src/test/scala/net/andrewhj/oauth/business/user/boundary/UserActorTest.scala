package net.andrewhj.oauth.business.user.boundary

import akka.actor.{ ActorRef, Props, ActorSystem }
import akka.testkit.{ ImplicitSender, DefaultTimeout, TestKit, TestActorRef }
import net.andrewhj.oauth.business.AkkaTestSuite
import net.andrewhj.oauth.business.user.boundary.UserMessages._
import net.andrewhj.oauth.business.user.controller.UserRepository
import net.andrewhj.oauth.business.user.entity.User
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import scala.concurrent.duration._

//class UserActorTest extends TestKit(ActorSystem("UserActorSpec")) with DefaultTimeout with ImplicitSender with WordSpecLike
//    with Matchers with MockFactory with BeforeAndAfterAll {
class UserActorTest extends AkkaTestSuite(ActorSystem("UserActorSpec")) {
  val actorProps = UserActor.withRepository _
  val mockRepo = stub[UserRepository]
  val userActor = system.actorOf(actorProps(mockRepo))

  "A User Actor" should {
    "With Empty DB" should {
      "Return empty when requesting all users" in {
        within(2 seconds) {
          (mockRepo.findAll _).when().returns(List.empty)
          userActor ! AllUsers
          val users = List.empty
          expectMsg(AllUserResult(users))
        }
      }
      "Return None when requesting non-existent username" in {
        within(2 seconds) {
          val username: String = "nothere"
          (mockRepo findOne _) when username returns None
          userActor ! FindUser(username)
          expectMsg(None)
        }
      }
    }
    "With Records" should {

      "Return list when requesting all users" in {
        val users: List[User] = List(User("test", Some("secret"), Some("test"), Some("user")))
        (mockRepo.findAll _).when().returns(users)
        within(2 seconds) {
          userActor ! AllUsers
          expectMsg(AllUserResult(users))
        }
      }
      "Return found user" in {
        val user = User("tst", Some("secret"), Some("Found"), Some("User"))
        (mockRepo findOne _) when "tst" returns Some(user)
        within(2 seconds) {
          userActor ! FindUser("tst")
          expectMsg(Some(user))
        }
      }
    }
    "Persist and return Created user" in {
      val user = User("newest")
      val newUserRepo = mock[UserRepository]
      val createActor: ActorRef = system.actorOf(actorProps(newUserRepo))
      newUserRepo.create _ expects user returning user
      within(2 seconds) {
        createActor ! CreateUser(user)
        expectMsg(UserCreated(user))
      }
    }
  }

}
