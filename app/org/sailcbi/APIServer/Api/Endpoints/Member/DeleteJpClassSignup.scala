package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import scala.concurrent.{ExecutionContext, Future}

class DeleteJpClassSignup @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, JpClassSignupDeletePostShape.apply)(parsed => {
			PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.juniorId, rc => {
				val pb: PersistenceBroker = rc.pb
				println(parsed)

				JPPortal.attemptDeleteSignup(pb, parsed.juniorId, parsed.instanceId) match {
					case ValidationOk => Future(Ok(new JsObject(Map("success" -> JsBoolean(true)))))
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
				}
			})
		})
	}

	case class JpClassSignupDeletePostShape (
		juniorId: Int,
		instanceId: Int
	)

	object JpClassSignupDeletePostShape {
		implicit val format = Json.format[JpClassSignupDeletePostShape]

		def apply(v: JsValue): JpClassSignupDeletePostShape = v.as[JpClassSignupDeletePostShape]
	}
}