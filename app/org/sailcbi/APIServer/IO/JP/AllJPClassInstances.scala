package org.sailcbi.APIServer.IO.JP

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Storable.StorableQuery.{QueryBuilder, TableAlias}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassSession, JpClassType}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

object AllJPClassInstances {
	def get(rc: StaffRequestCache)(implicit PA: PermissionsAuthority): List[JpClassSession] = {
		val types = TableAlias.wrapForInnerJoin(JpClassType)
		val instances = TableAlias.wrapForInnerJoin(JpClassInstance)
		val sessions = TableAlias.wrapForInnerJoin(JpClassSession)

		val sessionsQB = QueryBuilder
			.from(types)
			.innerJoin(instances, types.wrappedFields(_.fields.typeId).wrapFilter(_.equalsField(instances.wrappedFields(_.fields.typeId))))
			.innerJoin(sessions, instances.wrappedFields(_.fields.instanceId).wrapFilter(_.equalsField(sessions.wrappedFields(_.fields.instanceId))))
			.where(sessions.wrappedFields(_.fields.sessionDateTime).wrapFilter(_.isYearConstant(PA.currentSeason())))

		rc.executeQueryBuilder(sessionsQB).map(qbrr => {
			val session = JpClassSession.construct(qbrr)
			val instance = JpClassInstance.construct(qbrr)
			val classType = JpClassType.construct(qbrr)
			session.references.jpClassInstance.set(instance)
			instance.references.jpClassType.set(classType)

			classType.valuesList
			classType.referencesList

			instance.valuesList
			instance.referencesList

			session.valuesList
			session.referencesList

			session
		})
	}
}