package Entities

import java.time.LocalDateTime

import Entities.ApClassSession.self
import Storable.Fields.FieldValue.{DateTimeFieldValue, FieldValue, IntFieldValue}
import Storable.Fields.{DatabaseField, DateTimeDatabaseField, IntDatabaseField}
import Storable._

class ApClassSession extends StorableClass {
  val companion: StorableObject[ApClassSession] = ApClassSession

  object references extends ReferencesObject {
    var apClassInstance: Option[ApClassInstance] = None
  }
  object values extends ValuesObject {
    val sessionId = new IntFieldValue(ApClassSession.fields.sessionId)
    val instanceId = new IntFieldValue(ApClassSession.fields.instanceId)
    val sessionDateTime = new DateTimeFieldValue(ApClassSession.fields.sessionDateTime)
  }

  def setApClassInstance(v: ApClassInstance): Unit = references.apClassInstance = Some(v)
  def getApClassInstance: ApClassInstance = references.apClassInstance match {
    case Some(x) => x
    case None => throw new Exception("JpClassInstance unset for JpClassSession " + values.sessionId.get)
  }
}

object ApClassSession extends StorableObject[ApClassSession] {
  val entityName: String = "AP_CLASS_SESSIONS"

  object fields extends FieldsObject {
    val sessionId = new IntDatabaseField(self, "SESSION_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
  }

  val primaryKeyName: String = fields.sessionId.getFieldName

  def getSeedData: Set[ApClassSession] = Set(
    //  ApClassSession(1, 1, LocalDateTime.now)
  )
}