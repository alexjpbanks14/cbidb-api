package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import com.coleji.framework.Storable.Fields.DateTimeDatabaseField
import com.coleji.framework.Storable.StorableClass

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeFieldValue(instance: StorableClass, field: DateTimeDatabaseField)(implicit PA: PermissionsAuthority) extends FieldValue[LocalDateTime](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = PA.systemParams.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => ("'" + super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'", List.empty)
		case PERSISTENCE_SYSTEM_ORACLE => ("TO_DATE('" + super.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")) + "', 'MM/DD/YYYY HH:MI:SS')", List.empty)
	}
}
