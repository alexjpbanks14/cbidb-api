package org.sailcbi.APIServer.IO.JP

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Storable.StorableQuery.{QueryBuilder, TableAlias}
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

object AllJpClassSignups {
	def get(rc: StaffRequestCache, instanceIds: List[Int])(implicit PA: PermissionsAuthority): List[JpClassSignup] = {
		val types = TableAlias.wrapForInnerJoin(JpClassType)
		val instances = TableAlias.wrapForInnerJoin(JpClassInstance)
		val signups = TableAlias.wrapForInnerJoin(JpClassSignup)
		val persons = TableAlias.wrapForInnerJoin(Person)

		val groups = TableAlias.wrapForOuterJoin(JpGroup)
		val wlResults = TableAlias.wrapForOuterJoin(JpClassWlResult)
		val sections = TableAlias.wrapForOuterJoin(JpClassSection)
		val sectionLookups = TableAlias.wrapForOuterJoin(JpClassSectionLookup)

		val sessionsQB = QueryBuilder
			.from(types)
			.innerJoin(instances, JpClassType.fields.typeId.alias.equalsField(instances.wrappedFields(_.typeId)))
			.innerJoin(signups, JpClassInstance.fields.instanceId.alias.equalsField(signups.wrappedFields(_.instanceId)))
			.innerJoin(persons, JpClassSignup.fields.personId.alias.equalsField(persons.wrappedFields(_.personId)))

			.outerJoin(groups, JpClassSignup.fields.groupId.alias.equalsField(groups.wrappedFields(_.groupId)))
			.outerJoin(wlResults, JpClassSignup.fields.signupId.alias.equalsField(wlResults.wrappedFields(_.signupId)))
			.outerJoin(sections, JpClassSignup.fields.sectionId.alias.equalsField(sections.wrappedFields(_.sectionId)))
			.outerJoin(sectionLookups, JpClassSection.fields.lookupId.alias.equalsField(sectionLookups.wrappedFields(_.sectionId)))

			.where(JpClassInstance.fields.instanceId.alias.inList(instanceIds))
			// .where(instances.wrappedFields(_.instanceId).wrapFilter(_.equalsConstant(5361)))

			.select(
				QueryBuilder.allFieldsFromTable(types) ++
				QueryBuilder.allFieldsFromTable(instances) ++
				QueryBuilder.allFieldsFromTable(signups) ++
				QueryBuilder.allFieldsFromTable(wlResults) ++
				QueryBuilder.allFieldsFromTable(groups) ++
				QueryBuilder.allFieldsFromTable(sections) ++
				QueryBuilder.allFieldsFromTable(sectionLookups) ++
				List(
					persons.wrappedFields(_.personId),
					persons.wrappedFields(_.nameFirst),
					persons.wrappedFields(_.nameLast),
				)
			)

		val allSignups = rc.executeQueryBuilder(sessionsQB).map(qbrr => {
			val signup = signups.construct(qbrr)
			val instance = instances.construct(qbrr)
			val classType = types.construct(qbrr)
			val wlResult = wlResults.construct(qbrr)
			val group = groups.construct(qbrr)
			val person = persons.construct(qbrr)
			val section = sections.construct(qbrr)
			val sectionLookup = sectionLookups.construct(qbrr)

			signup.references.jpClassWlResult.set(wlResult)
			signup.references.jpClassInstance.set(instance)
			signup.references.group.set(group)
			signup.references.person.set(person)
			signup.references.section.set(section)
			section match {
				case Some(s) => s.references.sectionLookup.set(sectionLookup.get)
				case None =>
			}
			instance.references.jpClassType.set(classType)
			signup
		})
		allSignups
	}
}
