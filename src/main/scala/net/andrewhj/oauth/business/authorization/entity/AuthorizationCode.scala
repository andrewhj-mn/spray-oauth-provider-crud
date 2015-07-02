package net.andrewhj.oauth.business.authorization.entity

import java.util.{ Date, UUID }

case class AuthorizationCode(authorizationCode: UUID, clientId: UUID, userId: String, redirectUri: String, expires: Date, scope: Option[String])

case class RefreshToken(refreshToken: String, clientId: String, userId: Option[String], expires: Date, scope: Option[String])
