package Storable.Fields.FieldValue

import Storable.Fields.DoubleDatabaseField
import Storable.StorableClass

class DoubleFieldValue(instance: StorableClass, field: DoubleDatabaseField) extends FieldValue[Double](instance, field) {
  def getPersistenceLiteral: String = super.get.toString
}