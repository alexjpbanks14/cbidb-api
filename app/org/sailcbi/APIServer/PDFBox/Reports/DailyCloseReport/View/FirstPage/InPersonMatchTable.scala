package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.FirstPage

import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.ContentStreamDecorator
import org.sailcbi.APIServer.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_RIGHT, Drawable, DrawableTable}
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.DailyCloseReport
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.DailyCloseReportModel

class InPersonMatchTable(data: DailyCloseReportModel, maxWidth: Float, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, left: Float, top: Float) extends Drawable {
	val widthLeft = 80f
	val widthRight = 120f
	val header = DrawableTable(
		List(List("IN PERSON")),
		List(widthLeft + widthRight),
		List(ALIGN_CENTER),
		defaultBoldFont, defaultFontSize - 2
	)

	val cash: Currency = data.closeProps.inPersonRevenueTotal
	val tally: Currency = data.closeProps.inPersonTally
	val tape: Currency = data.closeProps.tapeValue

	val body = DrawableTable(
		List(List("Cash - DB:", DailyCloseReport.getMatchString(cash, tally)), List("Cash - POS:", DailyCloseReport.getMatchString(cash, tape))),
		List(widthLeft, widthRight),
		List(ALIGN_RIGHT, ALIGN_RIGHT),
		defaultFont, defaultFontSize - 2
	)

	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
		header.draw(contentStreamDecorator, left, top)
		body.draw(contentStreamDecorator, left, top - header.height)
	}
}
