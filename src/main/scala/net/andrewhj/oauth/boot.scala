package net.andrewhj.oauth
import java.util.concurrent.TimeUnit
import akka.actor.{ Props, ActorSystem }
import akka.io.IO
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http
import akka.pattern.ask
import scala.concurrent.Await

object Boot {
  implicit val system = ActorSystem("spray-oauth-provider-crud")

  def main(args: Array[String]): Unit = {

    class ApplicationServer(val actorSystem: ActorSystem) extends BootSystem with TopLevelRoute with ServerIO
    new ApplicationServer(system)

    sys.addShutdownHook(system.shutdown())
  }
}

/**
 * Binds http server to given host and port
 */
trait ServerIO {
  this: TopLevelRoute with BootSystem ⇒
  val config = ConfigFactory.load()

  IO(Http) ! Http.Bind(routeService, config.getString("application.server.host"), config.getInt("application.server.port"))
}

trait BootSystem {
  final val startupTimeout = 15

  implicit def actorSystem: ActorSystem
  implicit val timeout: Timeout = Timeout(startupTimeout, TimeUnit.SECONDS)

  /**
   * Initialize database, propagate schema
   */
  //  DatabaseConfig.init()

  val application = actorSystem.actorOf(Props[ApplicationActor], "application")
  Await.ready(application ? Startup(), timeout.duration)

  actorSystem.registerOnTermination {
    application ! Shutdown()
  }
}
