import java.util.UUID

import net.andrewhj.oauth.DatabaseCfg
import net.andrewhj.oauth.business.client.controller.SlickRelationalClientRepository
import net.andrewhj.oauth.business.client.entity.Client
import net.andrewhj.oauth.business.user.controller.Slick$UserRepository
import net.andrewhj.oauth.business.user.entity.User
import org.slf4j.LoggerFactory

trait Logging {
  val logger = LoggerFactory.getLogger(this.getClass)
}

object PopulateTables extends Logging {
  def main(args: Array[String]) {
    logger.info("Populating DB tables")
    logger.debug("users")
    val users = List(
      User("root", Some("root")),
      User("guest", Some("guest")),
      User("tom", Some("tomtom"), Some("tom"), Some("tom"))
    )
    for {
      u ← users
    } yield Slick$UserRepository.create(u)
    logger.debug("clients")
    val client = Client(UUID.fromString("3970313d-49ec-45b0-a47c-81188b0c2037"), "abcde", "localhost:8087/auth-step")
    SlickRelationalClientRepository.create(client)
    logger.info("Done.")
  }
}

object ClearTables {
  import scala.slick.driver.PostgresDriver.simple._
  import scala.slick.profile.SqlProfile
  import DatabaseCfg._

  def main(args: Array[String]) {
    db withSession { implicit session ⇒
      val q = for {
        u ← usersTable
      } yield u

      q.delete

      clientsTable.delete
    }
  }
}

object CreateDdl {
  private val logger = LoggerFactory.getLogger("main")

  def main(args: Array[String]) {
    logger.info("Initializing Postgres DDL")
    DatabaseCfg.create()
    logger.info("Done")
  }
}