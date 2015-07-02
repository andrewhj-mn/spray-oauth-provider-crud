package net.andrewhj.oauth

import spray.routing.Directives

/**
 * Created by ajohnson on 6/27/15.
 */
class HelloRoute extends Directives {
  val route = get {
    complete {
      "Hello"
    }
  }

}
