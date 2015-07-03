import org.scalatest.FunSpec
import spray.routing.HttpService
import spray.testkit.ScalatestRouteTest

abstract class FlatSpraySpec extends FunSpec with ScalatestRouteTest
// TODO: remove this when you don't need the example hanging around.
class BasicSprayTest extends FlatSpraySpec with HttpService {
  def actorRefFactory = system // connect the DSL to the test ActorSystem

  val smallRoute =
    get {
      pathSingleSlash {
        complete {
          <html>
            <body>
              <h1>Say hello to <i>spray</i>!</h1>
            </body>
          </html>
        }
      } ~
        path("ping") {
          complete("PONG!")
        }
    }

  describe("A basic spray system") {
    it("should return a greeting for GET requests to the root path") {
      Get() ~> smallRoute ~> check {
        assert(responseAs[String].contains("hello"))
      }
    }
    it("should return a 'PONG!' response for GET requests to /ping") {
      Get("/ping") ~> smallRoute ~> check {
        assert(responseAs[String] === "PONG!")
      }
    }
  }

}
