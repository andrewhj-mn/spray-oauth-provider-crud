package net.andrewhj.oauth.business.client.entity

import java.util.UUID

/**
 * An application with an API key that will request credential information from User
 * @param id
 * @param secret
 * @param redirectUri
 * @param grantTypes
 * @param scope
 * @param userId
 */
case class Client(id: UUID, secret: String, redirectUri: String, grantTypes: Option[String] = None,
  scope: Option[String] = None, userId: Option[String] = None)
