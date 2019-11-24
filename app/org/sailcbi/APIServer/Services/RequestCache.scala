package org.sailcbi.APIServer.Services

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.sailcbi.APIServer.Api.Endpoints.Member.AddJuniorClassReservationShape
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.{MembershipType, MembershipTypeExp, ProgramType, Rating}
import org.sailcbi.APIServer.IO.HTTP.FromWSClient
import org.sailcbi.APIServer.IO.StripeIOController
import org.sailcbi.APIServer.Logic.DateLogic
import org.sailcbi.APIServer.Services.Authentication._
import org.sailcbi.APIServer.Services.Exception.{CORSException, PostBodyNotJSONException}
import org.sailcbi.APIServer.Services.StripeAPIIO.{StripeAPIIOLiveService, StripeAPIIOMechanism}
import org.sailcbi.APIServer.Services.StripeDatabaseIO.StripeDatabaseIOMechanism
import play.api.libs.json.{JsNumber, JsObject, JsValue}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

// TODO: Some sort of security on the CacheBroker so arbitrary requests can't see the authentication tokens
// TODO: mirror all PB methods on RC so the RC can either pull from redis or dispatch to oracle etc
class RequestCache private[Services](
	val trueAuth: AuthenticationInstance,
	val auth: AuthenticationInstance,
	secrets: PermissionsAuthoritySecrets
)(implicit val PA: PermissionsAuthority) {
	private val self = this
	val pb: PersistenceBroker = {
		println("In RC:  " + PA.toString)
		val pbReadOnly = PA.readOnlyDatabase
		if (auth.userType == RootUserType) new OracleBroker(secrets.dbConnection, this, false, pbReadOnly)
		else new OracleBroker(secrets.dbConnection, this, PA.preparedQueriesOnly, pbReadOnly)
	}

	val cb: CacheBroker = new RedisBroker

	private def getStripeAPIIOMechanism(ws: WSClient)(implicit exec: ExecutionContext): StripeAPIIOMechanism = new StripeAPIIOLiveService(
		PermissionsAuthority.stripeURL,
		secrets.stripeSecretKey,
		new FromWSClient(ws)
	)

	private lazy val stripeDatabaseIOMechanism = new StripeDatabaseIOMechanism(pb)

	def getStripeIOController(ws: WSClient)(implicit exec: ExecutionContext): StripeIOController = new StripeIOController(
		this,
		getStripeAPIIOMechanism(ws),
		stripeDatabaseIOMechanism,
		PA.logger
	)

	// TODO: some way to confirm that things like this have no security on them (regardless of if we pass or fail in this req)
	// TODO: dont do this every request.
	object cachedEntities {
		lazy val programTypes: List[ProgramType] = pb.getAllObjectsOfClass(ProgramType)
		lazy val membershipTypes: List[MembershipType] = {
			pb.getAllObjectsOfClass(MembershipType).map(m => {
				m.references.program.findOneInCollection(programTypes)
				m
			})
		}
		lazy val membershipTypeExps: List[MembershipTypeExp] = {
			pb.getAllObjectsOfClass(MembershipTypeExp).map(me => {
				me.references.membershipType.findOneInCollection(membershipTypes)
				me
			})
		}
		lazy val ratings: List[Rating] = pb.getAllObjectsOfClass(Rating)
	}

	object logic {
		val dateLogic: DateLogic = new DateLogic(self)
	}

}

object RequestCache {
	// TODO: better way to handle requests authenticated against multiple mechanisms?
	// TODO: any reason this should be in a companion obj vs just in teh PA?  Seems like only the PA should be making these things

	// TODO: synchronizing this is a temp solution until i get thread pools figured out.
	//  Doesn't really solve any problems anyway, except making the log a bit easier to read
	def apply(
		requiredUserType: UserType,
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		rootCB: CacheBroker,
		secrets: PermissionsAuthoritySecrets
	 )(implicit PA: PermissionsAuthority): Option[RequestCache] = synchronized {
		println("\n\n====================================================")
		println("====================================================")
		println("====================================================")
		println("Path: " + parsedRequest.path)
		println("Request received: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
		println("Headers: " + parsedRequest.headers)
		// For all the enabled user types (besides public), see if the request is authenticated against any of them.
		val authentication: AuthenticationInstance = PA.authenticate(parsedRequest)

		// Cross-site request?
		// Someday I'll flesh this out more
		// For now, non-public requests may not be cross site
		// auth'd GETs are ok if the CORS status is Unknown
		val corsOK = {
			if (requiredUserType == StaffUserType || requiredUserType == MemberUserType) {
				CORS.getCORSStatus(parsedRequest.headers) match {
					case Some(UNKNOWN) => parsedRequest.method == ParsedRequest.methods.GET
					case Some(CROSS_SITE) | None => false
					case _ => true
				}
			} else true
		}

		if (!corsOK) {
			println("@@@  Nuking RC due to potential CSRF")
			throw new CORSException
		} else {
			println("@@@  CSRF check passed")
			println("Authenticated as " + authentication.userType)

			// requiredUserType says what this request endpoint requires
			// If we authenticated as a superior auth (i.e. a staff member requested a public endpoint),
			// attempt to downgrade to the desired auth
			// For public endpoints this is overkill but I may one day implement staff making reqs on member endpoints,
			// so this architecture will make that request behave exactly as if the member requested it themselves
			if (authentication.userType == requiredUserType) {
				Some(new RequestCache(authentication, authentication, secrets))
			} else {
				requiredUserType.getAuthFromSuperiorAuth(authentication, requiredUserName) match {
					case Some(lowerAuth: AuthenticationInstance) => {
						println("@@@ Successfully downgraded to " + lowerAuth.userType)
						Some(new RequestCache(
							trueAuth = authentication,
							auth = lowerAuth,
							secrets
						))
					}
					case None => {
						println("@@@ Unable to downgrade auth to " + requiredUserType)
						None
					}
				}
			}
		}
	}

	def parsePostBodyJSON[T](body: Option[JsValue], ctor: JsValue => T): T = {
		body match {
			case None => throw new PostBodyNotJSONException
			case Some(v: JsValue) => ctor(v)
			case Some(v) => throw new PostBodyNotJSONException
		}
	}
}
