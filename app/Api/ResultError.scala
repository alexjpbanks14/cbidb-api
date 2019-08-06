package Api

import play.api.libs.json.{JsObject, Json}

case class ResultError (
	code: String,
	message: String
) {
	implicit val format = ResultError.format
	def asJsObject(): JsObject = JsObject(Map("error" -> Json.toJson(this)))
}

object ResultError {
	implicit val format = Json.format[ResultError]
	val UNAUTHORIZED: JsObject = ResultError(
		code="unauthorized",
		message="Unauthorized"
	).asJsObject()
	val UNKNOWN: JsObject = ResultError(
		code="unknown",
		message="An internal error has occurred."
	).asJsObject()
}