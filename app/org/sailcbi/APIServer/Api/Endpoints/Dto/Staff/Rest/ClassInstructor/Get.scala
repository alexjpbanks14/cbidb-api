package org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Rest.ClassInstructor

import play.api.libs.json.{JsValue, Json}

/**
 * !!!!!!!!!!!!
 * This file is AUTO-GENERATED by cbidb-schema
 * Do not manually alter this file, or your changes will be lost
 * !!!!!!!!!!!!
 */
case class DtoStaffRestClassInstructorGetResponseSuccess (
	instructorId: Int,
	nameFirst: String,
	nameLast: String,
)

object DtoStaffRestClassInstructorGetResponseSuccess {
	implicit val format = Json.format[DtoStaffRestClassInstructorGetResponseSuccess]
	def apply(v: JsValue): DtoStaffRestClassInstructorGetResponseSuccess
		= v.as[DtoStaffRestClassInstructorGetResponseSuccess]
}

