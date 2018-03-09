package IO

import java.time.{LocalDateTime, ZoneId}

import CbiUtil.DateUtil
import Stripe.JsFacades.Charge
import play.api.libs.json.{JsArray, JsValue}
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest}

import scala.concurrent.{ExecutionContext, Future}

class StripeIO(baseURL: String, secretKey: String, ws: WSClient) (implicit exec: ExecutionContext) {
  def getCharges(since: Option[LocalDateTime]): Future[List[Charge]] = {
    def makeRequest(url: String, params: List[String], lastID: Option[String], results: List[Charge]): Future[List[Charge]] = {
      val finalParams: String = (lastID match {
        case None => params
        case Some(id) => ("starting_after=" + id) :: params
      }).mkString("&")

      val finalURL = if(finalParams.length == 0) url else url + "?" + finalParams

      val stripeRequest: WSRequest = ws.url(finalURL)
        .withAuth(secretKey, "", WSAuthScheme.BASIC)

      stripeRequest.get.flatMap(res => {
        val json = res.json.as[JsArray].value
        json.map(e => Stripe.JsFacades.Charge(e)).toList match {
          case Nil => Future {results}
          case l => makeRequest(url, params, Some(l.last.id), l ++ results)
        }
      })
    }

    val params = since match {
      case None => List.empty
      case Some(d) => List("created[gte]=" + DateUtil.toBostonTime(d).toEpochSecond)
    }
    makeRequest(baseURL + "charges", params, None, List.empty)
  }
}
