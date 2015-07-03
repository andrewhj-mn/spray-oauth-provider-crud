package net.andrewhj.oauth.helpers

trait RepositoryReads[A, B] {
  def findAll: List[A]
  def findOne(id: B): Option[A]
}

trait RepositoryWrites[A, B] {
  def create(a: A): A
  def update(a: A): A
  def delete(b: B): Unit
}

trait ReadWriteRepository[A, B] extends RepositoryReads[A, B] with RepositoryWrites[A, B]