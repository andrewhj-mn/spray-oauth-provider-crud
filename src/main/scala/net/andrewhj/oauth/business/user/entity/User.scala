package net.andrewhj.oauth.business.user.entity

import scala.slick.direct.AnnotationMapper.column
import scala.slick.model.Table
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.ProvenShape
import scala.slick.driver.JdbcProfile

/**
 * The starting point for oauth. These are the enrolled users in your system.
 * @param userName - required.
 * @param password - optional
 * @param firstName - optional (additional information about a user)
 * @param lastName - optional
 */
case class User(userName: String, password: Option[String] = None, firstName: Option[String] = None, lastName: Option[String] = None)

