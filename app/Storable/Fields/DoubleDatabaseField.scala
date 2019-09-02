package Storable.Fields

import Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL}
import Services._
import Storable.{Filter, ProtoStorable, StorableObject}

class DoubleDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[Double](entity, persistenceFieldName) {
	def getFieldType: String = PermissionsAuthority.getPersistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "decimal"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def findValueInProtoStorable(row: ProtoStorable): Option[Double] = {
		row.doubleFields.get(this.getRuntimeFieldName) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new Exception("non-null Double field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def lessThanConstant(c: Double): Filter = {
		Filter(getFullyQualifiedName + " < " + c)
	}

	def inList(l: List[Double]): Filter = PermissionsAuthority.getPersistenceSystem match {
		case r: PERSISTENCE_SYSTEM_RELATIONAL => {
			def groupIDs(ids: List[Double]): List[List[Double]] = {
				if (ids.length <= r.pbs.MAX_EXPR_IN_LIST) List(ids)
				else {
					val splitList = ids.splitAt(r.pbs.MAX_EXPR_IN_LIST)
					splitList._1 :: groupIDs(splitList._2)
				}
			}

			if (l.isEmpty) Filter("")
			else Filter(groupIDs(l).map(group => {
				getFullyQualifiedName + " in (" + group.mkString(", ") + ")"
			}).mkString(" OR "))
		}
	}

	def equalsConstant(d: Double): Filter = Filter(getFullyQualifiedName + " = " + d)

	def getValueFromString(s: String): Option[Double] = {
		try {
			val d = s.toDouble
			Some(d)
		} catch {
			case _: Throwable => None
		}
	}
}