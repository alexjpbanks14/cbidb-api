package org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Dockhouse.ScanCard

import play.api.libs.json.{JsValue, Json}

/**
 * !!!!!!!!!!!!
 * This file is AUTO-GENERATED by cbidb-schema
 * Do not manually alter this file, or your changes will be lost
 * !!!!!!!!!!!!
 */
case class DtoStaffDockhouseScanCardGetResponseSuccess (
	personId: Int,
	cardNumber: String,
	nameFirst: Option[String],
	nameLast: Option[String],
	bannerComment: Option[String],
	specialNeeds: Option[String],
	signoutBlockReason: Option[String],
	activeMemberships: List[DtoStaffDockhouseScanCardGetResponseSuccess_ActiveMemberships],
	personRatings: List[DtoStaffDockhouseScanCardGetResponseSuccess_PersonRatings],
	apClassSignupsToday: List[DtoStaffDockhouseScanCardGetResponseSuccess_ApClassSignupsToday],
	jpClassSignupsToday: List[DtoStaffDockhouseScanCardGetResponseSuccess_JpClassSignupsToday],
	maxFlagsPerBoat: List[DtoStaffDockhouseScanCardGetResponseSuccess_MaxFlagsPerBoat],
)

case class DtoStaffDockhouseScanCardGetResponseSuccess_ActiveMemberships (
	assignId: Int,
	membershipTypeId: Int,
	startDate: Option[String],
	expirationDate: Option[String],
	discountName: Option[String],
	isDiscountFrozen: Boolean,
	hasGuestPrivs: Boolean,
	programId: Int,
)

object DtoStaffDockhouseScanCardGetResponseSuccess_ActiveMemberships {
	implicit val format = Json.format[DtoStaffDockhouseScanCardGetResponseSuccess_ActiveMemberships]
	def apply(v: JsValue): DtoStaffDockhouseScanCardGetResponseSuccess_ActiveMemberships
		= v.as[DtoStaffDockhouseScanCardGetResponseSuccess_ActiveMemberships]
}

case class DtoStaffDockhouseScanCardGetResponseSuccess_PersonRatings (
	ratingId: Int,
	programId: Int,
	ratingName: String,
	status: String,
)

object DtoStaffDockhouseScanCardGetResponseSuccess_PersonRatings {
	implicit val format = Json.format[DtoStaffDockhouseScanCardGetResponseSuccess_PersonRatings]
	def apply(v: JsValue): DtoStaffDockhouseScanCardGetResponseSuccess_PersonRatings
		= v.as[DtoStaffDockhouseScanCardGetResponseSuccess_PersonRatings]
}

case class DtoStaffDockhouseScanCardGetResponseSuccess_ApClassSignupsToday (
	signupId: Int,
	instanceId: Int,
	personId: Int,
	signupType: String,
	signupDatetime: String,
	sequence: Int,
)

object DtoStaffDockhouseScanCardGetResponseSuccess_ApClassSignupsToday {
	implicit val format = Json.format[DtoStaffDockhouseScanCardGetResponseSuccess_ApClassSignupsToday]
	def apply(v: JsValue): DtoStaffDockhouseScanCardGetResponseSuccess_ApClassSignupsToday
		= v.as[DtoStaffDockhouseScanCardGetResponseSuccess_ApClassSignupsToday]
}

case class DtoStaffDockhouseScanCardGetResponseSuccess_JpClassSignupsToday (
	signupId: Int,
	instanceId: Int,
	personId: Int,
	signupType: String,
	signupDatetime: String,
	sequence: Double,
)

object DtoStaffDockhouseScanCardGetResponseSuccess_JpClassSignupsToday {
	implicit val format = Json.format[DtoStaffDockhouseScanCardGetResponseSuccess_JpClassSignupsToday]
	def apply(v: JsValue): DtoStaffDockhouseScanCardGetResponseSuccess_JpClassSignupsToday
		= v.as[DtoStaffDockhouseScanCardGetResponseSuccess_JpClassSignupsToday]
}

case class DtoStaffDockhouseScanCardGetResponseSuccess_MaxFlagsPerBoat (
	boatId: Int,
	programId: Int,
	maxFlag: String,
)

object DtoStaffDockhouseScanCardGetResponseSuccess_MaxFlagsPerBoat {
	implicit val format = Json.format[DtoStaffDockhouseScanCardGetResponseSuccess_MaxFlagsPerBoat]
	def apply(v: JsValue): DtoStaffDockhouseScanCardGetResponseSuccess_MaxFlagsPerBoat
		= v.as[DtoStaffDockhouseScanCardGetResponseSuccess_MaxFlagsPerBoat]
}

object DtoStaffDockhouseScanCardGetResponseSuccess {
	implicit val DtoStaffDockhouseScanCardGetResponseSuccess_ActiveMembershipsFormat
		= DtoStaffDockhouseScanCardGetResponseSuccess_ActiveMemberships.format
	implicit val DtoStaffDockhouseScanCardGetResponseSuccess_PersonRatingsFormat
		= DtoStaffDockhouseScanCardGetResponseSuccess_PersonRatings.format
	implicit val DtoStaffDockhouseScanCardGetResponseSuccess_ApClassSignupsTodayFormat
		= DtoStaffDockhouseScanCardGetResponseSuccess_ApClassSignupsToday.format
	implicit val DtoStaffDockhouseScanCardGetResponseSuccess_JpClassSignupsTodayFormat
		= DtoStaffDockhouseScanCardGetResponseSuccess_JpClassSignupsToday.format
	implicit val DtoStaffDockhouseScanCardGetResponseSuccess_MaxFlagsPerBoatFormat
		= DtoStaffDockhouseScanCardGetResponseSuccess_MaxFlagsPerBoat.format
	implicit val format = Json.format[DtoStaffDockhouseScanCardGetResponseSuccess]
	def apply(v: JsValue): DtoStaffDockhouseScanCardGetResponseSuccess
		= v.as[DtoStaffDockhouseScanCardGetResponseSuccess]
}

