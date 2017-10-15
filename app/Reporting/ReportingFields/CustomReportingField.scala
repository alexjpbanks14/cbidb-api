package Reporting.ReportingFields
import Services.PersistenceBroker
import Storable.StorableClass

class CustomReportingField[T <: StorableClass](fn: (T => String), fieldDisplayName: String) extends ReportingField[T](fieldDisplayName) {
  override def getValueFunction(pb: PersistenceBroker, instances: List[T]): T => String = fn
}
