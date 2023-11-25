package org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Rest.ClassInstructor

import play.api.libs.json.{JsValue, Json}

/**
 * !!!!!!!!!!!!
 * This file is AUTO-GENERATED by cbidb-schema
 * Do not manually alter this file, or your changes will be lost
 * !!!!!!!!!!!!
 */
case class DtoStaffRestClassInstructorPostResponseSuccess (
	instructorId: Int,
)

object DtoStaffRestClassInstructorPostResponseSuccess {
	implicit val format = Json.format[DtoStaffRestClassInstructorPostResponseSuccess]
	def apply(v: JsValue): DtoStaffRestClassInstructorPostResponseSuccess
		= v.as[DtoStaffRestClassInstructorPostResponseSuccess]
}


case class DtoStaffRestClassInstructorPostRequest (
	instructorId: Int,
	nameFirst: String,
	nameLast: String,
)

object DtoStaffRestClassInstructorPostRequest {
	implicit val format = Json.format[DtoStaffRestClassInstructorPostRequest]
	def apply(v: JsValue): DtoStaffRestClassInstructorPostRequest
		= v.as[DtoStaffRestClassInstructorPostRequest]
}

