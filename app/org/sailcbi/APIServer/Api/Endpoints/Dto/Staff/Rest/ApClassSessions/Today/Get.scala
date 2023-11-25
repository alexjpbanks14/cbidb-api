package org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Rest.ApClassSessions.Today

import play.api.libs.json.{JsValue, Json}

/**
 * !!!!!!!!!!!!
 * This file is AUTO-GENERATED by cbidb-schema
 * Do not manually alter this file, or your changes will be lost
 * !!!!!!!!!!!!
 */
case class StaffRestApClassSessionsTodayGetResponseSuccessDto (
	sessionId: Int,
	instanceId: Int,
	headcount: Option[Int],
	cancelledDatetime: Option[String],
	sessionDatetime: String,
	sessionLength: Double,
	isMakeup: Boolean,
	$$apClassInstance: StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance,
)

case class StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance (
	instanceId: Int,
	cancelledDatetime: Option[String],
	signupsStartOverride: Option[String],
	signupMin: Option[Int],
	price: Option[Double],
	signupMax: Option[Int],
	formatId: Int,
	hideOnline: Boolean,
	cancelByOverride: Option[String],
	locationString: Option[String],
	doNotAutoCancel: Boolean,
	$$apClassSignups: List[StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups],
)

case class StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups (
	instanceId: Int,
	discountInstanceId: Option[Int],
	voidedOnline: Boolean,
	personId: Int,
	orderId: Option[Int],
	price: Option[Double],
	signupId: Int,
	closeId: Option[Int],
	sequence: Int,
	paymentMedium: Option[String],
	ccTransNum: Option[String],
	paymentLocation: Option[String],
	voidCloseId: Option[Int],
	signupType: String,
	signupNote: Option[String],
	signupDatetime: String,
	$$person: StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_Person,
	$$apClassWaitlistResult: Option[StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_ApClassWaitlistResult],
)

case class StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_Person (
	personId: Int,
	nameFirst: Option[String],
	nameLast: Option[String],
)

object StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_Person {
	implicit val format = Json.format[StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_Person]
	def apply(v: JsValue): StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_Person
		= v.as[StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_Person]
}

case class StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_ApClassWaitlistResult (
	wlResult: String,
	foVmDatetime: Option[String],
	offerExpDatetime: String,
	signupId: Int,
	foAlertDatetime: String,
	permitOvercrowd: Boolean,
)

object StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_ApClassWaitlistResult {
	implicit val format = Json.format[StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_ApClassWaitlistResult]
	def apply(v: JsValue): StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_ApClassWaitlistResult
		= v.as[StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_ApClassWaitlistResult]
}

object StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups {
	implicit val StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_PersonFormat
		= StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_Person.format
	implicit val StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_ApClassWaitlistResultFormat
		= StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups_ApClassWaitlistResult.format
	implicit val format = Json.format[StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups]
	def apply(v: JsValue): StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups
		= v.as[StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups]
}

object StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance {
	implicit val StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignupsFormat
		= StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance_ApClassSignups.format
	implicit val format = Json.format[StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance]
	def apply(v: JsValue): StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance
		= v.as[StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance]
}

object StaffRestApClassSessionsTodayGetResponseSuccessDto {
	implicit val StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstanceFormat
		= StaffRestApClassSessionsTodayGetResponseSuccessDto_ApClassInstance.format
	implicit val format = Json.format[StaffRestApClassSessionsTodayGetResponseSuccessDto]
	def apply(v: JsValue): StaffRestApClassSessionsTodayGetResponseSuccessDto
		= v.as[StaffRestApClassSessionsTodayGetResponseSuccessDto]
}
