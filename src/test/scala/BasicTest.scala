import org.scalatest.{ FunSuite, FunSpec }

class BasicTest extends FunSuite {
  test("A simple validation") {
    assert(2 + 2 === 4)
  }

  test("A multiplication validation") {
    assert(3 * 3 === 9)
  }
}
