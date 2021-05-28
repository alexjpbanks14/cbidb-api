package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.framework.Util.NetSuccess
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{PaymentMethod, StripeError}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.Portal.PortalLogic.{RecurringDonation, SavedCardOrPaymentMethodData}
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsNumber, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RecurringDonationAmounts @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId
			val customerId = PortalLogic.getStripeCustomerId(rc, personId).get
			val stripe = rc.getStripeIOController(ws)
			val donations = PortalLogic.getRecurringDonations(rc, personId)

			stripe.getCustomerDefaultPaymentMethod(customerId).map({
				case s: NetSuccess[Option[PaymentMethod], StripeError] => {
					val cardDataMaybe = s.successObject.map(pm => SavedCardOrPaymentMethodData(
						last4 = pm.card.last4,
						expMonth = pm.card.exp_month,
						expYear = pm.card.exp_year,
						zip = pm.billing_details.address.postal_code
					))
					implicit val format = GetRecurringDonationsShape.format
					val resultJson: JsValue = Json.toJson(GetRecurringDonationsShape(
						recurringDonations=donations,
						paymentMethod=cardDataMaybe
					))

					Ok(resultJson)
				}
				case _ => {
					implicit val format = GetRecurringDonationsShape.format
					val resultJson: JsValue = Json.toJson(GetRecurringDonationsShape(
						recurringDonations=donations,
						paymentMethod=None
					))

					Ok(resultJson)
				}
			})
		})
	}

	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, GetRecurringDonationsShape.apply)(parsed => {
				val personId = rc.getAuthedPersonId
				PortalLogic.setRecurringDonations(rc, personId, parsed.recurringDonations)
				Future(Ok(new JsObject(Map(
					"success" -> JsBoolean(true)
				))))
			})
		})
	}

	case class GetRecurringDonationsShape (recurringDonations: List[RecurringDonation], paymentMethod: Option[SavedCardOrPaymentMethodData])
	object GetRecurringDonationsShape {
		implicit val format = Json.format[GetRecurringDonationsShape]
		def apply(v: JsValue): GetRecurringDonationsShape = v.as[GetRecurringDonationsShape]
	}
}
