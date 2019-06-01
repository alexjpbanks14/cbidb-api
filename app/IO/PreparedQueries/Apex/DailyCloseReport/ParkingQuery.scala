package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.Currency
import IO.PreparedQueries.HardcodedQueryForSelect
import PDFBox.Reports.DailyCloseReport.Model.ParkingData
import Services.Authentication.ApexUserType

class ParkingQuery(closeId: Int) extends HardcodedQueryForSelect[ParkingData](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select
		   |      nvl(open_ct,0) as open_ct,
		   |      nvl(close_ct,0) as close_ct,
		   |      nvl(change_ct,0) as change_ct,
		   |      nvl(comp_ct,0) as comp_ct,
		   |      nvl(open_ct,0) + nvl(change_ct,0) - nvl(close_ct,0) - nvl(comp_ct,0) as sold_ct,
		   |      100 * unit_price * (nvl(open_ct,0) + nvl(change_ct,0) - nvl(close_ct,0) - nvl(comp_ct,0)) as sold_value
		   |      from fo_parking
		   |      where close_id = $closeId
		   |      and (open_ct <> 0 or close_ct <> 0 or change_ct <> 0 or comp_ct <> 0)
		   |      order by 1
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSet): ParkingData = new ParkingData(
		open = rs.getInt(1),
		close = rs.getInt(2),
		plusMinus = rs.getInt(3),
		numberComp = rs.getInt(4),
		numberSold = rs.getInt(5),
		valueSold = Currency.cents(rs.getInt(6))
	)
}