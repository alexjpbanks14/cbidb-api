package org.sailcbi.APIServer.Reporting.ReportFactories

import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance.{JpClassInstanceFilterFactoryType, JpClassInstanceFilterFactoryYear}
import org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactory
import org.sailcbi.APIServer.Reporting.{ReportFactory, ReportingField}
import org.sailcbi.APIServer.Storable.StorableObject

import java.time.format.DateTimeFormatter

class ReportFactoryJpClassInstance extends ReportFactory[JpClassInstance] {
	lazy val jpClassTypes: List[JpClassType] = rc.getObjectsByFilters(JpClassType, List(), 20)

	lazy val jpClassSessions: List[JpClassSession] = rc.getObjectsByFilters(
		JpClassSession,
		List(JpClassSession.fields.instanceId.inList(getInstances.map(i => i.values.instanceId.get))),
		1000
	)

	val entityCompanion: StorableObject[JpClassInstance] = JpClassInstance

	def decorateInstancesWithParentReferences(instances: List[JpClassInstance]): Unit = {
		val types: Map[Int, JpClassType] =
			rc.getObjectsByFilters(JpClassType, List(), 20)
					.map(t => (t.getID, t))
					.foldLeft(Map(): Map[Int, JpClassType])((m: Map[Int, JpClassType], x: (Int, JpClassType)) => m + x)

		instances.foreach(i => {
			i.references.jpClassType.set(types(i.values.typeId.get))
		})
	}

	val fieldList: List[(String, ReportingField[JpClassInstance])] = List(
		("TypeId", ReportingField.getReportingFieldFromDatabaseField(JpClassInstance.fields.typeId, "Type ID", isDefault = true)),
		("TypeName", ReportingField.getReportingFieldFromDatabaseFieldParentObject[JpClassInstance, JpClassType](
			JpClassType.fields.typeName,
			i => i.references.jpClassType.get,
			"Type Name",
			isDefault = true
		)),
		("InstanceId", ReportingField.getReportingFieldFromDatabaseField(JpClassInstance.fields.instanceId, "Instance ID", isDefault = true)),
		("FirstSessionDatetime", new ReportingField[JpClassInstance](
			(i: JpClassInstance) =>
				jpClassSessions
						.filter(_.values.instanceId.get == i.getID)
						.map(_.values.sessionDateTime.get)
						.min
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
			"First Session Datetime",
			isDefault = true
		)),
		("SessionCt", new ReportingField[JpClassInstance](
			(i: JpClassInstance) => jpClassSessions.count(s => s.values.instanceId.get == i.values.instanceId.get).toString,
			"# Sessions",
			isDefault = false
		))
		/*, ("TypeDisplayOrder", ReportingField.getReportingFieldFromDatabaseFieldParentObject[JpClassInstance, JpClassType](
		  JpClassType.fields.displayOrder,
		  i => i.references.jpClassType.get,
		  "Type DisplayOrder",
		  isDefault = false
		))*/
	)

	val filterList: List[(String, ReportingFilterFactory[JpClassInstance])] = List(
		("JpClassInstanceFilterYear", new JpClassInstanceFilterFactoryYear()),
		("JpClassInstanceFilterType", new JpClassInstanceFilterFactoryType())
	)
}