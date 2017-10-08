package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{ProtoStorable, StorableObject}

class DoubleDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[Double](entity, persistenceFieldName) {
  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "decimal"
    case x if x == classOf[OracleBroker] => "number"
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[Double] = {
    row.doubleFields.get(this.getRuntimeFieldName) match {
      case Some(Some(x)) => Some(x)
      case Some(None) => throw new Exception("non-null Double field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
      case _ => None
    }
  }

  def getValueFromString(s: String): Option[Double] = {
    try {
      val d = s.toDouble
      Some(d)
    } catch {
      case _ => None
    }
  }
}