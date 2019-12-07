package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import scala.concurrent.{ExecutionContext, Future}

class JpClassSignup @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, JpClassSignupPostShape.apply)(parsed => {
			PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.juniorId, rc => {
				val pb: PersistenceBroker = rc.pb
				println(parsed)

				val doEnroll = parsed.doEnroll
				val wlJoin = doEnroll && JPPortal.canWaitListJoin(pb, parsed.juniorId, parsed.instanceId)

				//any:            see_type
				//any:            see_instance
				//any:            already_started
				//waitlist:       wl_exists
				//enroll:         spots_left
				//wljoin:         wl record exists
				//enroll/wljoin:  allow_enroll
				lazy val seeType = JPPortal.seeTypeFromInstanceIdAsValidationResult(pb, parsed.juniorId, parsed.instanceId)
				lazy val seeInstance = JPPortal.seeInstanceAsValidationResult(pb, parsed.juniorId, parsed.instanceId)
				lazy val alreadyStarted = JPPortal.alreadyStartedAsValidationResult(pb, parsed.instanceId)
				lazy val wlExistsOnClass = {
					if (doEnroll) ValidationOk
					else JPPortal.waitListExists(pb, parsed.instanceId)
				}
				lazy val hasSeats = {
					if (doEnroll && !wlJoin) JPPortal.hasSpotsLeft(pb, parsed.instanceId, Some("The class is full."))
					else ValidationOk
				}
				lazy val allowEnroll = {
					if (doEnroll) JPPortal.allowEnrollAsValidationResult(pb, parsed.juniorId, parsed.instanceId)
					else ValidationOk
				}

				val finalResult = for {
					_ <- seeType
					_ <- seeInstance
					_ <- alreadyStarted
					_ <- wlExistsOnClass
					_ <- hasSeats
					x <- allowEnroll
				} yield x

				finalResult match {
					case ValidationOk => {
						val signupId = JPPortal.actuallyEnroll(pb, parsed.instanceId, parsed.juniorId, None, doEnroll=doEnroll, fullEnroll = true, None).orNull
						Future(Ok(new JsObject(Map("signupId" -> JsNumber(signupId.toInt)))))
					}
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
				}
			})
		})
	}

	case class JpClassSignupPostShape (
		juniorId: Int,
		instanceId: Int,
		doEnroll: Boolean
	)

	object JpClassSignupPostShape {
		implicit val format = Json.format[JpClassSignupPostShape]

		def apply(v: JsValue): JpClassSignupPostShape = v.as[JpClassSignupPostShape]
	}
}

