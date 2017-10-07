package Entities

import Storable.Fields.FieldValue.{BooleanFieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{BooleanDatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

class User extends StorableClass {
  def companion: StorableObject[User] = User
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val userId = new IntFieldValue(User.fields.userId)
    val userName = new StringFieldValue(User.fields.userName)
    val nameFirst = new StringFieldValue(User.fields.nameFirst)
    val nameLast = new StringFieldValue(User.fields.nameLast)
    val active = new BooleanFieldValue(User.fields.active)
    val hideFromClose = new BooleanFieldValue(User.fields.hideFromClose)
  }
}

object User extends StorableObject[User] {
  val entityName: String = "USERS"

  object fields extends FieldsObject {
    val userId = new IntDatabaseField(self, "USER_ID")
    val userName = new StringDatabaseField(self, "USER_NAME", 50)
    val nameFirst = new StringDatabaseField(self, "NAME_FIRST", 100)
    val nameLast = new StringDatabaseField(self, "NAME_LAST", 100)
    val active = new BooleanDatabaseField(self, "ACTIVE")
    val hideFromClose = new BooleanDatabaseField(self, "HIDE_FROM_CLOSE")
  }

  val primaryKeyName: String = fields.userId.getFieldName

  def getSeedData: Set[User] = Set(
  //  User(1, "jcole", "Jon", "Cole", true, false),
  //  User(2, "gleib", "Ginger", "Leib", true, true),
  //  User(3, "czechel", "Charlie", "Zechel", true, false),
  //  User(4, "aalletag", "Andrew", "Alletag", true, false)
  )

  def construct(r: DatabaseRow)(implicit manifest: scala.reflect.Manifest[User]): User = super.construct(r)
}