package net.andrewhj.oauth.business.client.boundary

import java.util.UUID

import akka.actor.Actor.Receive
import akka.actor.{ Props, ActorLogging, Actor }

import net.andrewhj.oauth.business.client.controller.ClientRepository
import net.andrewhj.oauth.business.client.entity.Client

object ClientActor {
  case object FindAllClients
  case class FindClient(clientId: UUID)
  case class CreateClient(client: Client)

  case class ClientCreated(client: Client)

  def withRepository(clientRepository: ClientRepository) = Props(classOf[ClientActor], clientRepository)
}

class ClientActor(clientRepository: ClientRepository) extends Actor with ActorLogging {
  import net.andrewhj.oauth.business.client.boundary.ClientActor.{ FindClient, ClientCreated, CreateClient, FindAllClients }
  override def receive: Receive = {
    case FindAllClients ⇒ sender ! clientRepository.findAll
    case FindClient(id) ⇒ sender ! clientRepository.findOne(id)
    case CreateClient(c) ⇒
      val client = clientRepository.create(c)
      sender ! ClientCreated(client)
  }
}
