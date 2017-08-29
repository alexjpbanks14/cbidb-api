package Storable.Fields

import Services.PersistenceBroker
import Storable.{Filter, StorableObject}

abstract class DatabaseField[T](entity: StorableObject[_], fieldName: String) {
  def getFieldName: String = fieldName
  def getFullyQualifiedName: String = entity.entityName + "." + fieldName
  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String

  def isNull: Filter = Filter(getFullyQualifiedName + " IS NULL")
  def isNotNull: Filter = Filter(getFullyQualifiedName + " IS NOT NULL")
}
