package Services.Authentication

import CbiUtil.ParsedRequest
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}

abstract class UserType {
  // Given a request (and an unrestricted CacheBroker), determine if the request is authenticated against this mechanism.
  // Return Some(authenticated username) if so, None otherwise
  def getAuthenticatedUsernameInRequest(
    request: ParsedRequest,
    rootCB: CacheBroker,
    apexToken: String
  ): Option[String]

  // If the request actually came from e.g. a Staff request, but we want to access a Member or Public endpoint,
  // use this to downgrade the request authentication
  final def getAuthFromSuperiorAuth(
    currentAuthentication: AuthenticationInstance,
    requiredUserName: Option[String]
  ): Option[AuthenticationInstance] = getAuthenticatedUsernameFromSuperiorAuth(currentAuthentication, requiredUserName) match {
    case Some(userName) => Some(AuthenticationInstance(this, userName))
    case None => None
  }

  protected def getAuthenticatedUsernameFromSuperiorAuth(
    currentAuthentication: AuthenticationInstance,
    requiredUserName: Option[String]
  ): Option[String]

  // Given a username (and an unrestricted PersistenceBroker), get the (hashingGeneration, psHash) that is active for the user
  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)]

  def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility

  // TODO: this is not a good way to separate members from staff
  def getAuthenticatedUsernameInRequestFromCookie(request: ParsedRequest, rootCB: CacheBroker, apexToken: String): Option[String] = {
    val secCookies = request.cookies.filter(_.name == PermissionsAuthority.SEC_COOKIE_NAME)
    if (secCookies.isEmpty) None
    else if (secCookies.size > 1) None
    else {
      val cookie = secCookies.toList.head
      val token = cookie.value
      println("Found cookie on request: " + token)
      val cacheResult = rootCB.get(PermissionsAuthority.SEC_COOKIE_NAME + "_" + token)
      println(cacheResult)
      cacheResult match {
        case None => None
        case Some(s: String) => {
          val split = s.split(",")
          if (split.length != 2) None
          val userName = split(0)
          val expires = split(1)
          println("expires ")
          println(expires)
          println("and its currently ")
          println(System.currentTimeMillis())
          if (expires.toLong < System.currentTimeMillis()) None
          else Some(userName)
        }
      }
    }
  }
}
