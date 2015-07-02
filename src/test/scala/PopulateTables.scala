import net.andrewhj.oauth.DatabaseCfg
import net.andrewhj.oauth.business.user.controller.Slick$UserRepository
import net.andrewhj.oauth.business.user.entity.User
import org.slf4j.LoggerFactory

/**
 * Created by ajohnson on 6/28/15.
 */
object PopulateTables {
  def main(args: Array[String]) {
    val users = List(
      User("root", Some("root")),
      User("guest", Some("guest")),
      User("tom", Some("tomtom"), Some("tom"), Some("tom"))
    )
    for {
      u ← users
    } yield Slick$UserRepository.create(u)
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