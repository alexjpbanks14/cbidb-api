package org.sailcbi.APIServer.Services

case class PermissionsAuthoritySecrets(
	dbConnection: DatabaseConnection,
	apexToken: String,
	kioskToken: String,
	apexDebugSignet: Option[String],
	symonSalt: Option[String],
	stripeSecretKey: String
)