package Entities.EntityDefinitions

import CbiUtil.Initializable
import Storable.Fields.FieldValue.{DateTimeFieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DateTimeDatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

class JpClassSignup extends StorableClass {
  this.setCompanion(JpClassSignup)
  object references extends ReferencesObject {
    var jpClassInstance = new Initializable[JpClassInstance]
  }
  object values extends ValuesObject {
    val signupId = new IntFieldValue(self, JpClassSignup.fields.signupId)
    val instanceId = new IntFieldValue(self, JpClassSignup.fields.instanceId)
    val signupType = new StringFieldValue(self, JpClassSignup.fields.signupType)
    val personId = new IntFieldValue(self, JpClassSignup.fields.personId)
    val signupDatetime = new DateTimeFieldValue(self, JpClassSignup.fields.signupDatetime)
  }
}

object JpClassSignup extends StorableObject[JpClassSignup] {
  val entityName: String = "JP_CLASS_SIGNUPS"

  object fields extends FieldsObject {
    val signupId = new IntDatabaseField(self, "SIGNUP_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
    val personId = new IntDatabaseField(self, "PERSON_ID")
    val signupDatetime = new DateTimeDatabaseField(self, "SIGNUP_DATETIME")
  }

  def primaryKey: IntDatabaseField = fields.signupId
}