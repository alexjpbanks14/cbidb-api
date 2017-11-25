package Entities

import Storable.Fields.FieldValue.{DateFieldValue, IntFieldValue, NullableDoubleFieldValue, NullableIntFieldValue}
import Storable.Fields.{DateDatabaseField, IntDatabaseField, NullableDoubleDatabaseField, NullableIntDatabaseField}
import Storable._

class Donation extends StorableClass {
  this.setCompanion(Donation)
  object references extends ReferencesObject {
    var person: Option[Person] = None
  }
  object values extends ValuesObject {
    val donationId = new IntFieldValue(self, Donation.fields.donationId)
    val personId = new IntFieldValue(self, Donation.fields.personId)
    val amount = new NullableDoubleFieldValue(self, Donation.fields.amount)
    val donationDate = new DateFieldValue(self, Donation.fields.donationDate)
  }
}

object Donation extends StorableObject[Donation] {
  val entityName: String = "DONATIONS"

  object fields extends FieldsObject {
    val donationId = new IntDatabaseField(self, "DONATION_ID")
    val personId = new IntDatabaseField(self, "PERSON_ID")
    val amount = new NullableDoubleDatabaseField(self, "AMOUNT")
    val donationDate = new DateDatabaseField(self, "DONATION_DATE")
  }

  def primaryKey: IntDatabaseField = fields.donationId
}