package Storable.Fields.FieldValue

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.Fields.DateTimeDatabaseField
import Storable.StorableClass

class DateTimeFieldValue(instance: StorableClass, field: DateTimeDatabaseField) extends FieldValue[LocalDateTime](instance, field) {
  def getPersistenceLiteral(implicit pb: PersistenceBroker): String = pb match {
    case _: MysqlBroker => "'" + super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'"
    case _: OracleBroker => "TO_DATE('" + super.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")) + "', 'MM/DD/YYYY HH:MI:SS')"
  }
}
