package Api.Endpoints.PDFReports

import java.io.ByteArrayOutputStream
import java.time.ZonedDateTime

import Api.AuthenticatedRequest
import CbiUtil.{DateUtil, ParsedRequest}
import PDFBox.Reports.JpSpecialNeedsReport.JpSpecialNeedsReport
import PDFBox.Reports.JpSpecialNeedsReport.Loader.{JpSpecialNeedsReportLiveLoader, JpSpecialNeedsReportLiveParameter}
import Services.Authentication.ApexUserType
import Services.PermissionsAuthority
import akka.stream.scaladsl.Source
import akka.util.ByteString
import javax.inject.Inject
import org.apache.pdfbox.pdmodel.PDDocument
import play.api.http.HttpEntity
import play.api.mvc.{Action, AnyContent, ResponseHeader, Result}

class RunJpSpecialNeedsReport @Inject() extends AuthenticatedRequest {
	def get(from: String, to: String, signet: Option[String]): Action[AnyContent] = Action { req => {
		val fromZDT: ZonedDateTime = DateUtil.toBostonTime(DateUtil.parse(from))
		val toZDT: ZonedDateTime = DateUtil.toBostonTime(DateUtil.parse(to))
		val logger = PermissionsAuthority.logger
		val rc = getRC(
			ApexUserType,
			ParsedRequest(req)
					.addHeader("apex-signet", signet.getOrElse(""))
			/*.addHeader("pas-userName", userName)
			.addHeader("pas", pas)
			.addHeader("pas-procName", "DAILY_CLOSE_REPORT")
			.addHeader("pas-argString", "P_CLOSE_ID=" + closeId.toString + "&P_USER_NAME=" + userName)*/
		)
		val pb = rc.pb
		/* val verifyPas: Boolean =
		   pb.executePreparedQueryForSelect(new VerifyPas(userName, pas, "DAILY_CLOSE_REPORT", "P_CLOSE_ID=" + closeId.toString + "&P_USER_NAME=" + userName)).head

		 if (!verifyPas) throw new BadPasException
	 */
		val output = new ByteArrayOutputStream()
		val document: PDDocument = new PDDocument()

		val model = JpSpecialNeedsReportLiveLoader(JpSpecialNeedsReportLiveParameter(fromZDT, toZDT), pb)
		val report = new JpSpecialNeedsReport(model)
		report.appendToDocument(document)


		// Finally Let's save the PDF
		document.save(output)
		document.close()

		val source: Source[ByteString, _] = Source.single(ByteString(output.toByteArray))
		Result(
			header = ResponseHeader(200, Map()),
			body = HttpEntity.Streamed(source, Some(output.toByteArray.length), Some("application/pdf"))
		)
	}
	}
}
