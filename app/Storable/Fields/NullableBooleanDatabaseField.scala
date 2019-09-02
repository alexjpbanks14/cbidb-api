package Storable.Fields

import Services.PermissionsAuthority
import Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import Storable.{Filter, ProtoStorable, StorableObject}

class NullableBooleanDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[Option[Boolean]](entity, persistenceFieldName) {
	def getFieldLength: Int = 1

	def getFieldType: String = getFieldLength match {
		case l if l == 1 => "char(" + getFieldLength + ")"
		case _ => PermissionsAuthority.getPersistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => "char(1)"
			case PERSISTENCE_SYSTEM_ORACLE => "char(1)"
		}
	}

	def findValueInProtoStorable(row: ProtoStorable): Option[Option[Boolean]] = {
		row.stringFields.get(this.getRuntimeFieldName) match {
			case Some(Some("Y")) => Some(Some(true))
			case Some(Some("N")) => Some(Some(false))
			case Some(None) => Some(None)
			case _ => None
		}
	}

	def equals(b: Option[Boolean]): Filter = b match {
		case Some(x) => Filter(getFullyQualifiedName + " = '" + (if (x) "Y" else "N") + "'")
		case None => Filter(getFullyQualifiedName + " IS NULL")
	}

	def getValueFromString(s: String): Option[Option[Boolean]] = s.toLowerCase match {
		case "true" => Some(Some(true))
		case "false" => Some(Some(false))
		case "" => Some(None)
		case _ => None
	}
}