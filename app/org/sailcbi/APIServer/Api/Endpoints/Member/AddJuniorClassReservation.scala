package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.Api.{ValidationError, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddJuniorClassReservation @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(ProtoPersonUserType, None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(request.body.asJson, AddJuniorClassReservationShape.apply)(parsed => {
				doPost(rc, parsed) match {
					case Left(err) => Future(Ok(err.toResultError.asJsObject()))
					case Right(juniorId) => Future(Ok(new JsObject(Map(
						"personId" -> JsNumber(juniorId)
					))))
				}
			})
		})
	}

	private def doPost(rc: RequestCache, body: AddJuniorClassReservationShape): Either[ValidationError, Int] = {
		if (body.juniorFirstName == null || body.juniorFirstName.length() == 0) {
			Left(ValidationResult.from("Please specify junior name."))
		} else {
			val pb: PersistenceBroker = rc.pb

			// Create protoparent if it doenst exist
			val protoParentPersonId = ProtoPersonUserType.getAuthedPersonId(rc.auth.userName, pb)
			val parentPersonId = {
				if (protoParentPersonId.isDefined) {
					val ret = protoParentPersonId.get
					println("reusing existing protoparent record for this cookie val " + ret)
					ret
				} else {
					val ret = PortalLogic.persistProtoParent(pb, rc.auth.userName)
					println("created new protoparent: " + ret)
					ret
				}
			}

			val orderId = PortalLogic.getOrderId(pb, parentPersonId)

			// Create new protojunior
			val (juniorPersonId, rollbackCreateJunior) = PortalLogic.persistProtoJunior(
				pb,
				parentPersonId,
				body.juniorFirstName,
				body.beginnerInstanceId.isDefined || body.intermediateInstanceId.isDefined
			)

			// create new signup with the min(signup_time) of all this protoparent's other signups
			val minSignupTime = PortalLogic.getMinSignupTimeForParent(pb, parentPersonId)
			println("min signup is " + minSignupTime)
			val signupResult = PortalLogic.attemptSignupReservation(pb, juniorPersonId, body.beginnerInstanceId, body.intermediateInstanceId, minSignupTime, orderId)
			println(" result is:" + signupResult)
			if (signupResult.isDefined) {
				rollbackCreateJunior()
				Left(ValidationResult.from(signupResult.get))
			} else Right(juniorPersonId)
		}
	}
}
