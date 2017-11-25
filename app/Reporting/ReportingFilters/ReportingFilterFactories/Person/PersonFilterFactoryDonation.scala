package Reporting.ReportingFilters.ReportingFilterFactories.Person

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Entities.{Donation, Person}
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class PersonFilterFactoryDonation extends ReportingFilterFactory[Person] {
  val argTypes: List[ReportingFilterArgType] = List(ARG_DOUBLE, ARG_DATE)
  val displayName: String = "Donated at least $X since Y"
  val defaultValue: String = "4"
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    implicit val pb: PersistenceBroker = _pb

    type PersonID = Int

    val amount: Double = arg.split(",")(0).toDouble
    val sinceDate: LocalDate = LocalDate.parse(arg.split(", ")(1), DateTimeFormatter.ofPattern("MM/dd/yyyy"))

    val donationsSinceDate: List[Donation] = pb.getObjectsByFilters(
      Donation,
      List(
        Donation.fields.donationDate.greaterEqualConstant(sinceDate)
      )
    )

    val byPersonId: Map[Int, List[Donation]] = donationsSinceDate.groupBy(_.values.personId.get)

    val personIDs = byPersonId.filter(m => {
      m._2.foldLeft(0: Double)((sum, d) => {
        val donationAmount = d.values.amount.get match {
          case None => 0
          case Some(d) => d
        }
        sum + donationAmount
      }) >= amount
    }).keys.toList

    pb.getObjectsByIds(Person, personIDs).toSet
  })

}
