package org.sailcbi.APIServer.IO.PreparedQueries.Public

import java.sql.ResultSet

import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import play.api.libs.json.{JsArray, JsString}

class GetFlagColor extends HardcodedQueryForSelectCastableToJSObject[GetFlagColorResult](Set(PublicUserType)) {
	val getQuery: String =
		"""
		  |select flag from flag_changes where change_datetime = (select max(change_datetime) from flag_changes)
		""".stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSet): GetFlagColorResult = GetFlagColorResult(
		rs.getString(1),
	)

	val columnNames = List(
		"FLAG_COLOR",
	)

	def mapCaseObjectToJsArray(caseObject: GetFlagColorResult): JsArray = JsArray(IndexedSeq(
		JsString(caseObject.color),
	))
}

case class GetFlagColorResult(color: String)