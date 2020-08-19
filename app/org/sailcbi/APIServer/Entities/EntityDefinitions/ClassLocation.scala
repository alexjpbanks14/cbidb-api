package org.sailcbi.APIServer.Entities.EntityDefinitions

import org.sailcbi.APIServer.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import org.sailcbi.APIServer.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import org.sailcbi.APIServer.Storable._

class ClassLocation extends StorableClass {
	this.setCompanion(ClassLocation)

	object references extends ReferencesObject {}

	object values extends ValuesObject {
		val locationId = new IntFieldValue(self, ClassLocation.fields.locationId)
		val locationName = new StringFieldValue(self, ClassLocation.fields.locationName)
	}

	override val valuesList = List(
		values.locationId,
		values.locationName
	)
}

object ClassLocation extends StorableObject[ClassLocation] {
	val entityName: String = "CLASS_LOCATIONS"

	object fields extends FieldsObject {
		val locationId = new IntDatabaseField(self, "LOCATION_ID")
		val locationName = new StringDatabaseField(self, "LOCATION_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.locationId
}