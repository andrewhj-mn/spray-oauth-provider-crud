package net.andrewhj.oauth.business.user.controller

import net.andrewhj.oauth.business.user.entity.User
import net.andrewhj.oauth.helpers.ReadWriteRepository
import net.andrewhj.oauth.{ DatabaseCfg, Users }

import scala.slick.jdbc.JdbcBackend

trait UserRepository extends ReadWriteRepository[User, String] {
}

//abstract class UserRepositoryRelational(db: DatabaseComponent#DatabaseDef) extends UserRepository {
abstract class RelationalUserRepository(db: JdbcBackend#DatabaseDef) extends UserRepository {
  import scala.slick.driver.PostgresDriver.simple._

  val tableQuery = TableQuery[Users]
  override def findAll: List[User] = db withSession { implicit session ⇒
    val allUsers = tableQuery.run.toList
    allUsers
  }

  //  def findAll: List[User] = List.empty
  override def findOne(username: String): Option[User] = db withSession { implicit session ⇒
    tableQuery.filter(_.userName === username).run.headOption
  }

  override def create(a: User): User = db withSession { implicit session ⇒
    (tableQuery returning tableQuery) += a
  }

  override def update(a: User): User = ???

  //  override def update(a: User): User = db withSession { implicit session ⇒
  //    val q = for {
  //      user ← tableQuery if user.userName == a.userName
  //    } yield user.password.?
  //
  //    //      yield (user.password.?, user.firstName.?, user.lastName.?)
  //    q.update(a.password)
  //    //    q.update((a.password, a.firstName, a.lastName))
  //    //    q.update(a)
  //    //    tableQuery.filter(_.userName === a.userName).map(u => )
  //
  //    //    tableQuery.filter(_.userName === a.userName).update(a)
  //  }

  override def delete(b: String): Unit = db withSession { implicit session ⇒
    tableQuery.filter(_.userName === b).delete
  }
}

object Slick$UserRepository extends RelationalUserRepository(DatabaseCfg.db)

trait Config {
  def userRepository: UserRepository
}

// TODO: figure out how to use this for DI / (see http://blog.originate.com/blog/2013/10/21/reader-monad-for-dependency-injection/)
trait UserService {
  import scalaz.Reader

  def findAll = Reader((config: Config) ⇒ config.userRepository.findAll)
  def findOne(username: String) = Reader((config: Config) ⇒ config.userRepository.findOne(username))
  def create(user: User) = Reader((config: Config) ⇒ config.userRepository.create(user))
  def update(user: User) = Reader((config: Config) ⇒ config.userRepository.update(user))
  def delete(userName: String) = Reader((config: Config) ⇒ config.userRepository.delete(userName))
}

//object AJService extends UserRepository with UserService with UserRepositoryRelational(DatabaseCfg.db)