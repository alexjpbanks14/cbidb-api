package org.sailcbi.APIServer.Services

import org.sailcbi.APIServer.CbiUtil.PropertiesWrapper
import org.sailcbi.APIServer.Services.Authentication._

class ServerInstanceProperties(fileLocation: String) extends PropertiesWrapper(fileLocation, ServerInstanceProperties.requiredProperties) {
	// 3rd member is a function that returns true if the user type is permitted, false if we need to force-disable it even if conf says enable
	private val definedAuthMechanisms: Set[(UserType, String, () => Boolean)] = Set(
		(MemberUserType, "MemberAuthEnabled", () => true),
		(StaffUserType, "StaffAuthEnabled", () => true),
		(ApexUserType, "ApexAuthEnabled", () => true),
		(KioskUserType, "KioskAuthEnabled", () => true),
		(SymonUserType, "SymonAuthEnabled", () => getPropAsOption("SymonSalt").isDefined),
		(BouncerUserType, "BouncerAuthEnabled", () => true),
		(RootUserType, "RootAuthEnabled", () => true)
	)

	val enabledAuthMechanisms: Set[UserType] =
		definedAuthMechanisms
				.filter(t => getRequiredBoolean(t._2))
				.filter(t => t._3()) // check the nuke function
				.map(t => t._1)

	private def getRequiredBoolean(p: String): Boolean = getPropAsOption(p) match {
		case Some("true") => true
		case Some("false") => false
		case _ => throw new Exception("Required server property " + p + " was not set or not valid.")
	}

	private def getPropAsOption(p: String): Option[String] = {
		try {
			val prop = this.getProperty(p)
			if (prop == null) None
			else Some(prop)
		} catch {
			case _ => None
		}
	}
}

object ServerInstanceProperties {
	val requiredProperties: Array[String] = Array(
		"MemberAuthEnabled",
		"KioskAuthEnabled",
		"StaffAuthEnabled",
		"ApexAuthEnabled",
		"SymonAuthEnabled",
		"BouncerAuthEnabled",
		"RootAuthEnabled",
		"ApexToken",
		"ApexDebugSignet",
		"StripeAPIKey",
		"PreparedQueriesOnly",
		"RoutesSecurityLevel",
		"SymonSalt",
		"KioskToken"
	)
}