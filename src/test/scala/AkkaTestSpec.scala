package net.andrewhj.oauth.business

import akka.actor.ActorSystem
import akka.testkit.{ TestKit, ImplicitSender, DefaultTimeout }
import net.andrewhj.oauth.business.client.boundary.ClientActor
import net.andrewhj.oauth.business.client.controller.ClientRepository
import net.andrewhj.oauth.business.user.boundary.UserActor
import net.andrewhj.oauth.business.user.controller.UserRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

/**
 * Helper class to hold common ancestry and functions6 (should also make compilation faster)
 */
abstract class AkkaTestSpec(actorSystem: ActorSystem) extends TestKit(actorSystem) with DefaultTimeout with ImplicitSender with WordSpecLike with Matchers
    with MockFactory with BeforeAndAfterAll {

  override protected def afterAll(): Unit = shutdown()
}

abstract class ControllerTestSpec extends WordSpecLike with Matchers with MockFactory {
}
