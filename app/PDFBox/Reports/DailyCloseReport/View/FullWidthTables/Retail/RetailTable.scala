package PDFBox.Reports.DailyCloseReport.View.FullWidthTables.Retail

import PDFBox.Abstract.AbstractTable
import PDFBox.Drawable._
import PDFBox.Reports.DailyCloseReport.Model.RetailData
import org.apache.pdfbox.pdmodel.font.PDFont

class RetailTable(
						 data: List[RetailData],
						 headerFont: PDFont,
						 bodyFont: PDFont,
						 fontSize: Float
				 ) extends AbstractTable[RetailData](
	data,
	new MultiDrawableTable(List(DrawableTable(
		List(List("RETAIL & MISC ITEMS")),
		List(500f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Item Name", "# Sold", "Discounts", "$ Pretax", "$ Tax", "$ TOTAL", "# Comp", "$ Comp")),
		List(125f, 50f, 75f, 50f, 50f, 50f, 50f, 50f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT),
	bodyFont,
	fontSize,
	1,
	3
)