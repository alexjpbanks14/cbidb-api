package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.API.ValidationResult
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassSignup @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId
			PA.withParsedPostBodyJSON(request.body.asJson, ApClassSignupShape.apply)(parsed => {
				PortalLogic.apClassSignup(rc, personId, parsed.instanceId, parsed.doWaitlist) match {
					case None => Future(Ok(new JsObject(Map(
						"success" -> JsBoolean(true)
					))))
					case Some(err: String) => Future(Ok(ValidationResult.from(err).toResultError.asJsObject))
				}
			})
		})
	}

	case class ApClassSignupShape (
		instanceId: Int,
		doWaitlist: Boolean
	)

	object ApClassSignupShape {
		implicit val format = Json.format[ApClassSignupShape]

		def apply(v: JsValue): ApClassSignupShape = v.as[ApClassSignupShape]
	}
}