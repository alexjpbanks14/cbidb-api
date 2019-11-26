package org.sailcbi.APIServer.Services

import java.math.BigInteger
import java.security.MessageDigest
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import io.sentry.Sentry
import org.sailcbi.APIServer.Api.ResultError
import org.sailcbi.APIServer.CbiUtil.{Initializable, ParsedRequest}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForSelect, PreparedProcedureCall, PreparedQueryForSelect}
import org.sailcbi.APIServer.Services.Authentication._
import org.sailcbi.APIServer.Services.Emailer.SSMTPEmailer
import org.sailcbi.APIServer.Services.Exception.{PostBodyNotJSONException, UnauthorizedAccessException}
import org.sailcbi.APIServer.Services.Logger.{Logger, ProductionLogger, UnitTestLogger}
import org.sailcbi.APIServer.Services.PermissionsAuthority.PersistenceSystem
import play.api.libs.json.JsValue
import play.api.mvc.{Result, Results}

import scala.concurrent.{ExecutionContext, Future}


class PermissionsAuthority private[Services] (
	val serverParameters: ServerParameters,
	val isTestMode: Boolean,
	val readOnlyDatabase: Boolean,
	val allowableUserTypes: List[UserType],
	val preparedQueriesOnly: Boolean,
	val persistenceSystem: PersistenceSystem,
	secrets: PermissionsAuthoritySecrets
)  {
	println(s"inside PermissionsAuthority constructor: test mode: $isTestMode, readOnlyDatabase: $readOnlyDatabase")
	println(this.toString)
	// Initialize sentry
	Sentry.init(secrets.sentryDSN)
	def instanceName: String = secrets.dbConnection.mainSchemaName

	def sleep(): Unit = {
//		println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
//		println("sleepytime...")
//		println("Active threads: " + Thread.activeCount())
//		println("Current thread ID: " + Thread.currentThread().getId)
//		println("Current thread name: " + Thread.currentThread().getName)
//		println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
//		Thread.sleep(4000)
	}

	private lazy val rootRC: RequestCache = new RequestCache(AuthenticationInstance.ROOT, AuthenticationInstance.ROOT, secrets)
	// TODO: should this ever be used except by actual root-originated reqs e.g. crons?
	// e.g. there are some staff/member accessible functions that ultimately use this (even if they cant access rootPB directly)
	private lazy val rootPB = rootRC.pb
	private lazy val rootCB = new RedisBroker

	private lazy val bouncerRC: RequestCache = new RequestCache(AuthenticationInstance.BOUNCER, AuthenticationInstance.BOUNCER, secrets)
	private lazy val bouncerPB = bouncerRC.pb

	def now(): LocalDateTime = {
		val q = new PreparedQueryForSelect[LocalDateTime](Set(RootUserType)) {
			override val params: List[String] = List()

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDateTime = rsw.getLocalDateTime(1)

			override def getQuery: String = "select util_pkg.get_sysdate from dual"
		}
		rootPB.executePreparedQueryForSelect(q).head
	}

	def testDB = rootPB.testDB

	def procedureTest() = {
		println("starting executeProcedure...")
		val ret = rootPB.executeProcedure(PreparedProcedureCall.test)
		println("finished executeProcedure")

		println("procedure call complete with named params.....")
		println(s"(b, c, ss, persons) = $ret")
	}

	def logger: Logger = if (!isTestMode) new ProductionLogger(new SSMTPEmailer(Some("jon@community-boating.org"))) else new UnitTestLogger

	def requestIsFromLocalHost(request: ParsedRequest): Boolean = {
		val allowedIPs = Set(
			"127.0.0.1",
			"0:0:0:0:0:0:0:1"
		)
		allowedIPs.contains(request.remoteAddress)
	}

	private def wrapInStandardTryCatch(block: () => Future[Result])(implicit exec: ExecutionContext): Future[Result] = {
		try {
			block()
		} catch {
			case _: UnauthorizedAccessException => Future(Results.Status(400)(ResultError.UNAUTHORIZED))
			case e: PostBodyNotJSONException => {
				Sentry.capture(e)
				Future(Results.Status(400)(ResultError.NOT_JSON))
			}
			case e: Throwable => {
				logger.error(e.getMessage, e)
				Sentry.capture(e)
				Future(Results.Status(400)(ResultError.UNKNOWN))
			}
		}
	}

	private def withRCWrapper(
		get: () => Option[RequestCache],
		block: RequestCache => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] = {
		wrapInStandardTryCatch(() => get() match {
			case None => Future(Results.Ok(ResultError.UNAUTHORIZED))
			case Some(rc) => block(rc)
		})
	}

	private def getRequestCache(
		requiredUserType: NonMemberUserType,
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest
	): Option[RequestCache] =
		RequestCache(
			requiredUserType,
			requiredUserName,
			parsedRequest,
			rootCB,
			secrets
		)

	def withRequestCache(
		requiredUserType: NonMemberUserType,
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		block: RequestCache => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] =
		withRCWrapper(() => getRequestCache(requiredUserType, requiredUserName, parsedRequest), block)

	private def getRequestCacheMember(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest
	): Option[RequestCache] =
		RequestCache(MemberUserType, requiredUserName, parsedRequest, rootCB, secrets)

	def withRequestCacheMember(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		block: RequestCache => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] =
		withRCWrapper(() => getRequestCacheMember(requiredUserName, parsedRequest), block)

	private def getRequestCacheMemberWithJuniorId(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		juniorId: Int
	): Option[RequestCache] = {
		println("about to validate junior id in request....")
		RequestCache(MemberUserType, requiredUserName, parsedRequest, rootCB, secrets) match {
			case None => None
			case Some(ret) => {
				if (ret.auth.userType == MemberUserType) {
					// auth was successful
					//... but does the request juniorId match the auth'd parent id?
					val authedPersonId = MemberUserType.getAuthedPersonId(ret.auth.userName, rootPB)
					val getAuthedJuniorIDs = new HardcodedQueryForSelect[Int](Set(RootUserType)) {
						override def getQuery: String =
							s"""
							   |select b from person_relationships rl
							   |where a = ${authedPersonId}
							   |and rl.type_id = ${MagicIds.PERSON_RELATIONSHIP_TYPE_PARENT_WITH_ACCT_LINK}
					""".stripMargin
						override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)
					}
					val juniorIds = rootPB.executePreparedQueryForSelect(getAuthedJuniorIDs)
					if (juniorIds.contains(juniorId)) {
						Some(ret)
					} else {
						throw new Exception(s"junior ID ${juniorId} in request does not match allowed ids for parent ${authedPersonId}: ${juniorIds.mkString(", ")}")
					}
				} else {
					// auth wasn't successful anyway
					Some(ret)
				}
			}
		}
	}

	def withRequestCacheMemberWithJuniorId(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		juniorId: Int,
		block: RequestCache => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] =
		withRCWrapper(() => getRequestCacheMemberWithJuniorId(requiredUserName, parsedRequest, juniorId), block)

	private def getRequestCacheMemberWithParentId(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		parentId: Int
	): Option[RequestCache] = {
		RequestCache(MemberUserType, requiredUserName, parsedRequest, rootCB, secrets) match {
			case None => None
			case Some(ret) => {
				if (ret.auth.userType == MemberUserType) {
					// auth was successful
					//... but does the request parentId match the auth'd parent id?
					val authedPersonId = MemberUserType.getAuthedPersonId(ret.auth.userName, rootPB)
					if (authedPersonId == parentId) {
						Some(ret)
					} else {
						throw new Exception(s"parent ID ${parentId} in request does not match authed parent ID ${authedPersonId}")
					}
				} else {
					// auth wasn't successful anyway
					Some(ret)
				}
			}
		}
	}

	def withRequestCacheMemberWithParentId(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		parentId: Int,
		block: RequestCache => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] =
		withRCWrapper(() => getRequestCacheMemberWithParentId(requiredUserName, parsedRequest, parentId), block)

	def getPwHashForUser(request: ParsedRequest, userName: String, userType: UserType): Option[(Int, String)] = {
		if (
			allowableUserTypes.contains(userType) && // requested user type is enabled in this server instance
					authenticate(request).userType == BouncerUserType
		) userType.getPwHashForUser(userName, bouncerPB)
		else None
	}

	def validateSymonHash(
		host: String,
		program: String,
		argString: String,
		status: Int,
		mac: String,
		candidateHash: String
	): Boolean = {
		println("here we go")
		val now: String = ZonedDateTime.now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH").withZone(ZoneId.of("America/New_York")))
		val input = secrets.symonSalt.get + List(host, program, argString, status.toString, mac, now).mkString("-") + secrets.symonSalt.get
		println(input)
		val md5Bytes = MessageDigest.getInstance("MD5").digest(input.getBytes)
		val expectedHash = String.format("%032X", new BigInteger(1, md5Bytes))
		println("expectedHash: " + expectedHash)
		println("candidateHash: " + candidateHash)
		expectedHash == candidateHash
	}

	def validateApexSignet(candidate: Option[String]): Boolean = secrets.apexDebugSignet == candidate

	def authenticate(parsedRequest: ParsedRequest): AuthenticationInstance = {
		val ret: Option[AuthenticationInstance] = allowableUserTypes
				.filter(_ != PublicUserType)
				.foldLeft(None: Option[AuthenticationInstance])((retInner: Option[AuthenticationInstance], ut: UserType) => retInner match {
					// If we already found a valid auth mech, pass it through.  Else hand the auth mech our cookies/headers etc and ask if it matches
					case Some(x) => Some(x)
					case None => ut.getAuthenticatedUsernameInRequest(parsedRequest, rootCB, secrets.apexToken, secrets.kioskToken) match {
						case None => None
						case Some(x: String) => {
							println("AUTHENTICATION:  Request is authenticated as " + ut)
							Some(AuthenticationInstance(ut, x))
						}
					}
				})

		// If after looping through all auth mechs we still didnt find a match, this request is Public
		ret match {
			case Some(x) => x
			case None => {
				println("AUTHENTICATION:  No auth mechanisms matched; this is a Public request")
				AuthenticationInstance(PublicUserType, PublicUserType.uniqueUserName)
			}
		}
	}

	def assertRC(auth: AuthenticationInstance): RequestCache = {
		if (!isTestMode) throw new Exception("assertRC is for unit testing only")
		else new RequestCache(auth, auth, secrets)
	}

	def closeDB(): Unit = secrets.dbConnection.close()

	def withParsedPostBodyJSON[T](body: Option[JsValue], ctor: JsValue => T)(block: T => Future[Result])(implicit exec: ExecutionContext): Future[Result] = {
		wrapInStandardTryCatch(() => {
			body match {
				case None => throw new PostBodyNotJSONException
				case Some(v: JsValue) => block(ctor(v))
				case Some(_) => throw new PostBodyNotJSONException
			}
		})
	}
}


object PermissionsAuthority {
	private var paWrapper: Initializable[PermissionsAuthority] = new Initializable[PermissionsAuthority]()
	def setPA(pa: PermissionsAuthority): PermissionsAuthority = paWrapper.set(pa)
	def isBooted: Boolean = paWrapper.isInitialized
	implicit def PA: PermissionsAuthority = paWrapper.get
	def clearPA(): Unit = {
		println("in clearPA()")
		if (isBooted && paWrapper.get.isTestMode) {

			this.paWrapper = new Initializable[PermissionsAuthority]()
		}
	}

	val stripeURL: String = "https://api.stripe.com/v1/"
	val SEC_COOKIE_NAME = "CBIDB-SEC"
	val ROOT_AUTH_HEADER = "origin-root"
	val BOUNCER_AUTH_HEADER = "origin-bouncer"

	val EFUSE_REDIS_KEY_CBIDB_PUBLIC_WEB = "$$CBIDB_PUBLIC_WEB_EFUSE"

	trait PersistenceSystem {
		val pbs: PersistenceBrokerStatic
	}

	trait PERSISTENCE_SYSTEM_RELATIONAL extends PersistenceSystem {
		override val pbs: RelationalBrokerStatic
	}

	case object PERSISTENCE_SYSTEM_ORACLE extends PERSISTENCE_SYSTEM_RELATIONAL {
		val pbs: RelationalBrokerStatic = OracleBrokerStatic
	}

	case object PERSISTENCE_SYSTEM_MYSQL extends PERSISTENCE_SYSTEM_RELATIONAL {
		val pbs: RelationalBrokerStatic = MysqlBrokerStatic
	}


}
