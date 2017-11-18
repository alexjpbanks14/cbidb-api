package Api.Endpoints.ReportingAPI

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequest
import Reporting.Report
import Services.ServerStateWrapper.ServerState
import Services.{CacheBroker, PersistenceBroker, ServerStateWrapper}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.HttpEntity
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class RunReport @Inject() (ssw: ServerStateWrapper) (implicit exec: ExecutionContext) extends Controller {
  implicit val ss: ServerState = ssw.get
  implicit val pb: PersistenceBroker = ss.pa.pb
  implicit val cb: CacheBroker = ss.pa.cb

  object OUTPUT_TYPE {
    val JSCON = "jscon"
    val TSV = "tsv"
  }

  val errorResult = JsObject(Map("data" -> JsString("error")))

  def getTest(): Action[AnyContent] = get("ApClassType", "", "TypeName,TypeId", "jscon")

  def get(baseEntityString: String, filterSpec: String, fieldSpec: String, outputType: String): Action[AnyContent] = Action.async {
    lazy val request = new ReportRequest(baseEntityString, filterSpec, fieldSpec, outputType)
    outputType match {
      case OUTPUT_TYPE.JSCON => request.getFuture.map(s => Ok(s).as("application/json"))
      case OUTPUT_TYPE.TSV => Future {
        val reportResult: String = request.report.formatTSV
        val source: Source[ByteString, _] = Source.single(ByteString(reportResult))
        Result(
          header = ResponseHeader(200, Map(
            CONTENT_DISPOSITION -> "attachment; filename=report.tsv"
          )),
          body = HttpEntity.Streamed(source, Some(reportResult.length), Some("application/text"))
        )
      }
      case _ => Future{Ok(errorResult).as("application/json")}
    }
  }

  class ReportRequest(baseEntityString: String, filterSpec: String, fieldSpec: String, outputType: String) extends ApiRequest(cb) {
    def getCacheBrokerKey: CacheKey = "report_" + baseEntityString + "_" + filterSpec + "_" + fieldSpec + "_" + outputType

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusSeconds(5)
    }

    object params {}

    lazy val report: Report = Report.getReport(pb, baseEntityString, filterSpec, fieldSpec)

    def getJSONResultFuture: Future[JsObject] = Future {
      outputType match {
        case OUTPUT_TYPE.JSCON => report.formatJSCON
        case OUTPUT_TYPE.TSV => JsObject(Map(
          "tsv" -> JsString(report.formatTSV)
        ))
        case _ => errorResult
      }
    }
  }
}