package com.coleji.framework.Storable.Fields

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import com.coleji.framework.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class StringDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String, fieldLength: Int)(implicit PA: PermissionsAuthority) extends DatabaseField[String](entity, persistenceFieldName) {
	def getFieldLength: Int = fieldLength

	def isNullable: Boolean = false

	def getFieldType: String = getFieldLength match {
		case l if l == 1 => "char(" + getFieldLength + ")"
		case _ => PA.systemParams.persistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => "varchar(" + getFieldLength + ")"
			case PERSISTENCE_SYSTEM_ORACLE => "varchar2(" + getFieldLength + ")"
		}
	}

	def findValueInProtoStorableImpl[T](row: ProtoStorable[T], key: T): Option[String] = {
		row.stringFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new NonNullFieldWasNullException("non-null String field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def equalsConstant(c: String): String => Filter =
		t => Filter(s"$t.$getPersistenceFieldName = ?", List(c))


	def equalsConstantLowercase(c: String): String => Filter =
		t => Filter(s"lower($t.$getPersistenceFieldName) = ?", List(c.toLowerCase()))

	def getValueFromString(s: String): Option[String] = Some(s)

//	def alias(tableAlias: TableAliasInnerJoined): ColumnAliasInnerJoined[String, StringDatabaseField] = ColumnAliasInnerJoined(tableAlias, this)
//	def alias(tableAlias: TableAliasOuterJoined): ColumnAliasOuterJoined[String, StringDatabaseField] = ColumnAliasOuterJoined(tableAlias, this)
}