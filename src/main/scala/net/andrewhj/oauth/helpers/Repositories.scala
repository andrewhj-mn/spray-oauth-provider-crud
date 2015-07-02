package net.andrewhj.oauth.helpers

trait ReadOnlyRepository[A, B] {
  def findAll: List[A]
  def findOne(id: B): Option[A]
}

trait CrudRepository[A, B] extends ReadOnlyRepository[A, B] {
  def create(a: A): A
  def update(a: A): A
  def delete(b: B): Unit
}
