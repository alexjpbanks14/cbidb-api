package org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Rest.ApClassTypes

import play.api.libs.json.{JsValue, Json}

/**
 * !!!!!!!!!!!!
 * This file is AUTO-GENERATED by cbidb-schema
 * Do not manually alter this file, or your changes will be lost
 * !!!!!!!!!!!!
 */
case class StaffRestApClassTypesGetResponseSuccessDto (
	typeId: Int,
	typeName: String,
	ratingPrereq: Option[Int],
	classPrereq: Option[Int],
	ratingOverkill: Option[Int],
	displayOrder: Double,
	descLong: String,
	descShort: Option[String],
	classOverkill: Option[Int],
	noSignup: Boolean,
	priceDefault: Option[Double],
	signupMaxDefault: Option[Int],
	signupMinDefault: Option[Int],
	disallowIfOverkill: Boolean,
	$$apClassFormats: List[StaffRestApClassTypesGetResponseSuccessDto_ApClassFormats],
)

case class StaffRestApClassTypesGetResponseSuccessDto_ApClassFormats (
	formatId: Int,
	typeId: Int,
	description: Option[String],
	priceDefaultOverride: Option[Double],
	sessionCtDefault: Double,
	sessionLengthDefault: Double,
	signupMaxDefaultOverride: Option[Double],
	signupMinDefaultOverride: Option[Double],
)

object StaffRestApClassTypesGetResponseSuccessDto_ApClassFormats {
	implicit val format = Json.format[StaffRestApClassTypesGetResponseSuccessDto_ApClassFormats]
	def apply(v: JsValue): StaffRestApClassTypesGetResponseSuccessDto_ApClassFormats
		= v.as[StaffRestApClassTypesGetResponseSuccessDto_ApClassFormats]
}

object StaffRestApClassTypesGetResponseSuccessDto {
	implicit val StaffRestApClassTypesGetResponseSuccessDto_ApClassFormatsFormat
		= StaffRestApClassTypesGetResponseSuccessDto_ApClassFormats.format
	implicit val format = Json.format[StaffRestApClassTypesGetResponseSuccessDto]
	def apply(v: JsValue): StaffRestApClassTypesGetResponseSuccessDto
		= v.as[StaffRestApClassTypesGetResponseSuccessDto]
}

