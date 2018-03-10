package Api.Endpoints.Stripe

import javax.inject.Inject

import Api.AuthenticatedRequest
import CbiUtil.ParsedRequest
import IO.PreparedQueries.Apex._
import Services.Authentication.ApexUserType
import Services.{PermissionsAuthority, ServerStateContainer}
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.{ExecutionContext, Future}

class CreateChargeFromToken @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  def post(): Action[AnyContent] = Action.async {r => doPost(ParsedRequest(r))}

  def doPost(req: ParsedRequest): Future[Result] = {
    val ultimateErrMsg = "An internal error occurred.  Tech support has been notified and this issue will be resolved within 24 hours.  Please do not attempt to resubmit payment."
    try {
      val rc = getRC(ApexUserType, req)
      val pb = rc.pb
      val params = req.postParams
      val token: String = params("token")
      val orderId: Int = params("orderId").toInt

      val orderDetails: GetCartDetailsForOrderIdResult = pb.executePreparedQuery(new GetCartDetailsForOrderId(orderId)).head
      val tokenRecord: ValidateTokenInOrderResult = pb.executePreparedQuery(new ValidateTokenInOrder(orderId, token)).head
      val closeID: Int = pb.executePreparedQuery(new GetCurrentOnlineClose).head.closeId

      val stripeRequest: WSRequest = ws.url(PermissionsAuthority.stripeURL + "charges")
        .withAuth(PermissionsAuthority.secrets.stripeAPIKey.get(rc), "", WSAuthScheme.BASIC)
      val futureResponse: Future[WSResponse] = stripeRequest.post(Map(
        "amount" -> orderDetails.priceInCents.toString,
        "currency" -> "usd",
        "source" -> token,
        "description" -> ("Charge for orderId " + orderId + " time " + ServerStateContainer.get.nowDateTimeString),
        "metadata[closeId]" -> closeID.toString,
        "metadata[orderId]" -> orderId.toString,
        "metadata[token]" -> token,
        "metadata[cbiInstance]" -> PermissionsAuthority.instanceName.get
      ))
      futureResponse.map(r => {
        println(r.json.toString())
        try {
          val chargeObject = Stripe.JsFacades.Charge(r.json)
          val msg = List("success", chargeObject.id, chargeObject.amount).mkString("$$")
          Ok(msg)
        }catch {
          case e: Throwable => {
            println(e)
            try {
              val errorObject = Stripe.JsFacades.Error(r.json)
              val msg = List("failure", errorObject.`type`, errorObject.message).mkString("$$")
              Ok(msg)
            } catch {
              case f: Throwable => {
                println(f)
                Ok(List("failure", "cbi-api-error", f.getMessage).mkString("$$"))
              }
            }
          }
        }
      })
    } catch {
      case e: Throwable => {
        println(e)
        Future {Ok(List("failure", "cbi-api-error", e.getMessage).mkString("$$"))}
      }
    }

  }
}


