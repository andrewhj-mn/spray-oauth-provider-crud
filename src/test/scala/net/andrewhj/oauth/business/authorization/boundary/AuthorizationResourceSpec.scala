package net.andrewhj.oauth.business.authorization.boundary

import java.util.UUID
import java.util.concurrent.TimeUnit

import net.andrewhj.oauth.{ ResourceService, BootSystem }
import org.scalatest.{ FeatureSpec, GivenWhenThen, Matchers }
import spray.http.StatusCodes
import spray.routing.HttpService
import spray.testkit.ScalatestRouteTest

import scala.concurrent.duration.FiniteDuration

abstract class AkkaHttpTestSuite extends FeatureSpec with GivenWhenThen with Matchers with ScalatestRouteTest
    with HttpService with ResourceService with BootSystem {
  implicit val routeTestTimeout: RouteTestTimeout = RouteTestTimeout(FiniteDuration(5, TimeUnit.SECONDS))
  implicit def actorSystem = system
  def actorRefFactory = system

}

class AuthorizationResourceSpec extends AkkaHttpTestSuite {
  info("As a client")
  info("I want to be able to authenticate a user through my application")
  info("So I can re-use existing sign-on and permissions")

  feature("Asks for authorization code") {
    ignore("offer valid client credentials (happy path)") {
      Given("A valid, registered client id")
      val clientId = UUID.fromString("bca86394-5502-43fb-8b64-529c1f6e72e1")
      val responseType = "code"
      val redirectUri = "http://www.google.com"

      When("invoking Authorization Resource")
      Get(s"/oauth/authorize?response_type=$responseType&client_id=$clientId&redirect_uri=$redirectUri") ~> sealRoute(routes) ~> check {
        Then("Redirect to the provided URI and issue Authorization Code")
        //        assert(status == StatusCodes.Forbidden)
        assert(status == StatusCodes.Found)
        //        assert(response.pa)
      }

    }

    ignore("Forbid unregistered clients") {
      Given("a client")
      val clientId = UUID.fromString("bca86394-5502-43fb-8b64-529c1f6e72e1")
      val responseType = "code"
      val redirectUri = "http://www.google.com"

      When("invoking Authorization")
      Get(s"/oauth/authorize?response_type=$responseType&client_id=$clientId&redirect_uri=$redirectUri") ~> sealRoute(routes) ~> check {
        Then("Forbid")
        assert(status === StatusCodes.Forbidden)
      }
    }
  }

}
