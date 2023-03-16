package org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Dockhouse.ScanCard

import play.api.libs.json.{JsValue, Json}

/**
 * !!!!!!!!!!!!
 * This file is AUTO-GENERATED by cbidb-schema
 * Do not manually alter this file, or your changes will be lost
 * !!!!!!!!!!!!
 */
case class StaffDockhouseScanCardGetResponseSuccessDto (
	personId: Int,
	cardNumber: String,
	nameFirst: String,
	nameLast: String,
	bannerComment: Option[String],
	specialNeeds: Option[String],
	signoutBlockReason: Option[String],
	activeMemberships: List[StaffDockhouseScanCardGetResponseSuccessDto_ActiveMemberships],
	personRatings: List[StaffDockhouseScanCardGetResponseSuccessDto_PersonRatings],
	apClassSignupsToday: List[StaffDockhouseScanCardGetResponseSuccessDto_ApClassSignupsToday],
	jpClassSignupsToday: List[StaffDockhouseScanCardGetResponseSuccessDto_JpClassSignupsToday],
	maxFlagsPerBoat: List[StaffDockhouseScanCardGetResponseSuccessDto_MaxFlagsPerBoat],
)

case class StaffDockhouseScanCardGetResponseSuccessDto_ActiveMemberships (
	assignId: Int,
	membershipTypeId: Int,
	startDate: Option[String],
	expirationDate: Option[String],
	discountName: Option[String],
	isDiscountFrozen: Boolean,
	hasGuestPrivs: Boolean,
)

object StaffDockhouseScanCardGetResponseSuccessDto_ActiveMemberships {
	implicit val format = Json.format[StaffDockhouseScanCardGetResponseSuccessDto_ActiveMemberships]
	def apply(v: JsValue): StaffDockhouseScanCardGetResponseSuccessDto_ActiveMemberships
		= v.as[StaffDockhouseScanCardGetResponseSuccessDto_ActiveMemberships]
}

case class StaffDockhouseScanCardGetResponseSuccessDto_PersonRatings (
	ratingId: Int,
	programId: Int,
	ratingName: String,
	status: String,
)

object StaffDockhouseScanCardGetResponseSuccessDto_PersonRatings {
	implicit val format = Json.format[StaffDockhouseScanCardGetResponseSuccessDto_PersonRatings]
	def apply(v: JsValue): StaffDockhouseScanCardGetResponseSuccessDto_PersonRatings
		= v.as[StaffDockhouseScanCardGetResponseSuccessDto_PersonRatings]
}

case class StaffDockhouseScanCardGetResponseSuccessDto_ApClassSignupsToday (
	signupId: Int,
	instanceId: Int,
	personId: Int,
	signupType: String,
	signupDatetime: String,
	sequence: Int,
)

object StaffDockhouseScanCardGetResponseSuccessDto_ApClassSignupsToday {
	implicit val format = Json.format[StaffDockhouseScanCardGetResponseSuccessDto_ApClassSignupsToday]
	def apply(v: JsValue): StaffDockhouseScanCardGetResponseSuccessDto_ApClassSignupsToday
		= v.as[StaffDockhouseScanCardGetResponseSuccessDto_ApClassSignupsToday]
}

case class StaffDockhouseScanCardGetResponseSuccessDto_JpClassSignupsToday (
	signupId: Int,
	instanceId: Int,
	personId: Int,
	signupType: String,
	signupDatetime: String,
	sequence: Int,
)

object StaffDockhouseScanCardGetResponseSuccessDto_JpClassSignupsToday {
	implicit val format = Json.format[StaffDockhouseScanCardGetResponseSuccessDto_JpClassSignupsToday]
	def apply(v: JsValue): StaffDockhouseScanCardGetResponseSuccessDto_JpClassSignupsToday
		= v.as[StaffDockhouseScanCardGetResponseSuccessDto_JpClassSignupsToday]
}

case class StaffDockhouseScanCardGetResponseSuccessDto_MaxFlagsPerBoat (
	boatId: Int,
	programId: Int,
	maxFlag: String,
)

object StaffDockhouseScanCardGetResponseSuccessDto_MaxFlagsPerBoat {
	implicit val format = Json.format[StaffDockhouseScanCardGetResponseSuccessDto_MaxFlagsPerBoat]
	def apply(v: JsValue): StaffDockhouseScanCardGetResponseSuccessDto_MaxFlagsPerBoat
		= v.as[StaffDockhouseScanCardGetResponseSuccessDto_MaxFlagsPerBoat]
}

object StaffDockhouseScanCardGetResponseSuccessDto {
	implicit val StaffDockhouseScanCardGetResponseSuccessDto_ActiveMembershipsFormat
		= StaffDockhouseScanCardGetResponseSuccessDto_ActiveMemberships.format
	implicit val StaffDockhouseScanCardGetResponseSuccessDto_PersonRatingsFormat
		= StaffDockhouseScanCardGetResponseSuccessDto_PersonRatings.format
	implicit val StaffDockhouseScanCardGetResponseSuccessDto_ApClassSignupsTodayFormat
		= StaffDockhouseScanCardGetResponseSuccessDto_ApClassSignupsToday.format
	implicit val StaffDockhouseScanCardGetResponseSuccessDto_JpClassSignupsTodayFormat
		= StaffDockhouseScanCardGetResponseSuccessDto_JpClassSignupsToday.format
	implicit val StaffDockhouseScanCardGetResponseSuccessDto_MaxFlagsPerBoatFormat
		= StaffDockhouseScanCardGetResponseSuccessDto_MaxFlagsPerBoat.format
	implicit val format = Json.format[StaffDockhouseScanCardGetResponseSuccessDto]
	def apply(v: JsValue): StaffDockhouseScanCardGetResponseSuccessDto
		= v.as[StaffDockhouseScanCardGetResponseSuccessDto]
}

