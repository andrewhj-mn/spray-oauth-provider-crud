package net.andrewhj.oauth.business.client.boundary

import java.util.UUID

import akka.actor.ActorSystem
import net.andrewhj.oauth.business.AkkaTestSuite
import net.andrewhj.oauth.business.client.boundary.ClientActor.{ FindClient, ClientCreated, CreateClient, FindAllClients }
import net.andrewhj.oauth.business.client.controller.ClientRepository
import net.andrewhj.oauth.business.client.entity.Client
import scala.concurrent.duration._
import org.scalamock._

class ClientActorTest extends AkkaTestSuite(ActorSystem("ClientActorSpec")) {
  val actorProps = ClientActor.withRepository _
  val mockRepo = stub[ClientRepository]
  val userActor = system.actorOf(actorProps(mockRepo))

  "A client actor" should {
    "return empty with no clients" in {
      within(2 seconds) {
        (mockRepo.findAll _).when().returns(List.empty)
        userActor ! FindAllClients
        expectMsg(List.empty)
      }
    }
    "return clients when present" in {
      within(2 seconds) {
        val clientList = List(Client(UUID.randomUUID(), "secret1", "google.com"), Client(UUID.randomUUID(), "secret2", "facebook.com"))
        (mockRepo.findAll _).when().returns(clientList)
        userActor ! FindAllClients
        expectMsg(clientList)
      }
    }

    "return none without valid client" in {
      val uuid = UUID.randomUUID()
      (mockRepo.findOne _).when(uuid).returns(None)
      userActor ! FindClient(uuid)
      expectMsg(None)
    }

    "return specific client when existing" in {
      within(2 seconds) {
        val uuid = UUID.randomUUID()
        val client = Client(uuid, "secret1", "google.com")
        (mockRepo.findOne _).when(uuid).returns(Some(client))
        userActor ! FindClient(uuid)
        expectMsg(Some(client))
      }
    }

    "create clients on request" in {
      within(2 seconds) {
        val newClient = Client(UUID.randomUUID(), "testone", "localhost:3000")
        (mockRepo.create _).when(newClient).returns(newClient)
        userActor ! CreateClient(newClient)
        expectMsg(ClientCreated(newClient))
      }
    }
  }
}
