package net.andrewhj.oauth

import java.util.UUID

import net.andrewhj.oauth.business.client.entity.Client
import net.andrewhj.oauth.business.user.entity.User
import org.slf4j.{ LoggerFactory, Logger }
import scala.slick.driver.PostgresDriver.simple._

object CreateDdl {
  private val logger = LoggerFactory.getLogger("main")

  def main(args: Array[String]) {
    logger.info("Initializing Postgres DDL")
    DatabaseCfg.init()
    logger.info("Done")
  }
}

object DropDdl {
  private val logger = LoggerFactory.getLogger("DropDdl")

  def main(args: Array[String]) {
    logger.info("Removing Postgres DDL")
    DatabaseCfg.drop()
    logger.info("done")
  }
}

object BootstrapData {
  private val logger = LoggerFactory.getLogger("BootstrapData")

  import DatabaseCfg._

  def main(args: Array[String]) {
    logger.info("populating data")
    val superUser = User("root", Some("root"), Some("Super"), Some("User"))
    val guest = User("guest", Some("guest"), None, None)

    val authorizationCodeGrantType = "code"

    val client = Client(UUID.randomUUID(), "secret", "http://fake", Some(authorizationCodeGrantType))
    val client2 = Client(UUID.randomUUID(), "secret2", "http://fake2", Some(authorizationCodeGrantType))
    val client3 = Client(UUID.randomUUID(), "secret3", "http://fake3", Some(authorizationCodeGrantType))

    db withSession { implicit session ⇒
      usersTable ++= Seq(superUser, guest)
      clientsTable ++= Seq(client, client2, client3)
    }
    logger.info("Done")

  }

}

object DisplayClients {
  private val logger = LoggerFactory.getLogger("DisplayClients")

  import DatabaseCfg._

  def main(args: Array[String]) {
    logger.info("Clients:")
    db withSession { implicit session ⇒
      val clients = clientsTable.list

      clients.foreach { c: Client ⇒
        logger.info(c.toString)
        println(c)
      }
      logger.info(s"Total: ${clients.length}")
    }
  }
}