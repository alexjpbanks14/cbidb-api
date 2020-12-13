package org.sailcbi.APIServer.Api.Endpoints.Security

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Authentication.{MemberUserType, StaffUserType, UserType}
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetPwHashForUser @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(userName: String, userType: String = "staff")(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		Future {
			println("userType is " + userType)
			println("username is " + userName)
			val userTypeObj: Option[UserType] = userType match {
				case "staff" => {
					println("looking up staff")
					Some(StaffUserType)
				}
				case "member" => {
					println("looking up member")
					Some(MemberUserType)
				}
				case _ => None
			}
			println("headers: " + request.headers)
			if (userTypeObj.isEmpty) Ok("BAD USER TYPE")
			else {
				try {
					PA.getPwHashForUser(ParsedRequest(request), userName, userTypeObj.get) match {
						case None => Ok("NO DATA")
						// Int is the hashing scheme ID, string is the hash itself
						case Some(t: (Int, String)) => Ok(t._1 + "," + t._2)
					}
				} catch {
					case e: Exception => {
						println(e)
						Ok("NO DATA")
					}
				}

			}
		}
	}
}
