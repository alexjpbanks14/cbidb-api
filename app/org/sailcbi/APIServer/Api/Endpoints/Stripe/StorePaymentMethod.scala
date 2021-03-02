package org.sailcbi.APIServer.Api.Endpoints.Stripe

import org.sailcbi.APIServer.Api.ResultError
import org.sailcbi.APIServer.CbiUtil.{CriticalError, NetSuccess, ParsedRequest, ValidationError}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.StripeError
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class StorePaymentMethod @Inject()(implicit exec: ExecutionContext, ws: WSClient) extends InjectedController {
	def postAP()(implicit PA: PermissionsAuthority): Action[AnyContent] = post("AP")
	def postJP()(implicit PA: PermissionsAuthority): Action[AnyContent] = post("JP")

	private def post(program: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCacheMember(parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, StorePaymentMethodShape.apply)(parsed => {

				val stripe = rc.getStripeIOController(ws)
				val personId = rc.getAuthedPersonId()
				val orderId = PortalLogic.getOrderId(rc, personId, program)
				val totalInCents = PortalLogic.getOrderTotalCents(rc, orderId)
				val customerIdOption = PortalLogic.getStripeCustomerId(rc, personId)
				customerIdOption match {
					case None => Future(Ok("fail"))
					case Some(customerId) => {
						stripe.storePaymentMethod(customerId, parsed.paymentMethodId).flatMap({
							case ve: ValidationError[_, StripeError] => {
								if (ve.errorObject.`type` == "card_error") {
									Future(Ok(ResultError("process_err", ve.errorObject.message).asJsObject()))
								} else {
									throw new Exception("non-card stripe error: " + ve.errorObject)
								}
							}
							case e: CriticalError[_, StripeError] => throw e.e
							case _: NetSuccess[_, StripeError] => PortalLogic.updateAllPaymentIntentsWithNewMethod(rc, personId, parsed.paymentMethodId, stripe).map(_ => {
								// TODO: this is a list of SRRs; we should confirm they all worked
								Ok(JsObject(Map("success" -> JsBoolean(true))))
							})
						})
					}
				}
			})
		})
	}

	case class StorePaymentMethodShape(paymentMethodId: String, retryLatePayments: Boolean)

	object StorePaymentMethodShape {
		implicit val format = Json.format[StorePaymentMethodShape]

		def apply(v: JsValue): StorePaymentMethodShape = v.as[StorePaymentMethodShape]
	}
}
