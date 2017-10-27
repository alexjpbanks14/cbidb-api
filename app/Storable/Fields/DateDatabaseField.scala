package Storable.Fields

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Services.{MysqlBroker, OracleBroker, PersistenceBroker, RelationalBroker}
import Storable.{Filter, ProtoStorable, StorableObject}

class DateDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[LocalDate](entity, persistenceFieldName) {
  val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  def getFieldType(implicit pb: PersistenceBroker): String = pb match {
    case _: RelationalBroker => "date"
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[LocalDate] = {
    row.dateFields.get(this.getRuntimeFieldName) match {
      case Some(Some(x)) => Some(x)
      case Some(None) => throw new Exception("non-null Date field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
      case _ => None
    }
  }

  def isYearConstant(year: Int)(implicit pb: PersistenceBroker): Filter = pb match {
    case _: MysqlBroker => {
      val jan1 = LocalDate.of(year, 1, 1)
      val nextJan1 = LocalDate.of(year+1, 1, 1)
      Filter(getFullyQualifiedName + ">= " + jan1.format(standardPattern) + " AND " + getFullyQualifiedName + " < " + nextJan1.format(standardPattern))
    }
    case _: OracleBroker => Filter("TO_CHAR(" + getFullyQualifiedName + ", 'YYYY') = " + year)
  }

  def isDateConstant(date: LocalDate)(implicit pb: PersistenceBroker): Filter = pb match {
    case _: MysqlBroker =>
      Filter(getFullyQualifiedName + " = '" + date.format(standardPattern) + "'")
    case _: OracleBroker =>
      Filter("TRUNC(" + getFullyQualifiedName + ") = TO_DATE('" + date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "','MM/DD/YYYY')")
  }

  def getValueFromString(s: String): Option[LocalDate] = {
    try {
      Some(LocalDate.parse(s, standardPattern))
    } catch {
      case _: Throwable => None
    }
  }
}