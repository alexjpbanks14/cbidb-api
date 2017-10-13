package Api.Endpoints

import javax.inject.Inject

import Entities.{ApClassFormat, ApClassInstance, ApClassType}
import Reporting.ReportingFields.ApClassInstance.ApClassInstanceReportingFieldSessionCount
import Reporting.ReportingFields.ReportingField
import Reporting.ReportingFilters.ApClassInstance.{ApClassInstanceFilter, ApClassInstanceFilterType, ApClassInstanceFilterYear}
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class Report @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action {
    val instances: Set[ApClassInstance] = {
      val thisYear: ApClassInstanceFilter = new ApClassInstanceFilterYear(pb, 2017)
      val jibClasses: ApClassInstanceFilter = new ApClassInstanceFilterType(pb, 7)
      val jib2Classes: ApClassInstanceFilter = new ApClassInstanceFilterType(pb, 8)
      println("this year " + thisYear.instances.size)
      println("jib " + jibClasses.instances.size)
      println("jib2 " + jib2Classes.instances.size)
      println("jib union jib2 " + jibClasses.or(jib2Classes).instances.size)
      println("thisyear union jib " + thisYear.or(jibClasses).instances.size)
      println("thisyear intersect jib " + thisYear.and(jibClasses).instances.size)

      thisYear.and(jibClasses.or(jib2Classes)).instances
    }

    instances.foreach(i => {
      pb.getObjectById(ApClassFormat, i.values.formatId.get) match {
        case Some(f: ApClassFormat) => i.setApClassFormat(f)
      }
      val format: ApClassFormat = i.references.apClassFormat.get
      pb.getObjectById(ApClassType, format.values.typeId.get) match {
        case Some(t: ApClassType) => format.setApClassType(t)
      }
    })

    println("@#@#@# " + instances.size)

    val fields: List[ReportingField[ApClassInstance]] = List(
      ReportingField.getReportingFieldFromDatabaseFieldParentObject[ApClassInstance, ApClassFormat](ApClassFormat.fields.typeId, i => i.references.apClassFormat.get),
      ReportingField.getReportingFieldFromDatabaseField(ApClassInstance.fields.instanceId),
      new ApClassInstanceReportingFieldSessionCount
    )

    println(new Reporting.Report[ApClassInstance](instances, fields).getReport(pb))

    Ok("hi")
  }
}
