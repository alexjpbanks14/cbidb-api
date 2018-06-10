package PDFBox.Reports.JpClassRoster.View

import PDFBox.Abstract.AbstractTable
import PDFBox.Drawable._
import PDFBox.Reports.JpClassRoster.Model.{JpClassRosterModel, JpRosterData}
import org.apache.pdfbox.pdmodel.font.PDFont

class JpClassRosterView(
  data: JpClassRosterModel,
  roster: List[JpRosterData],
  headerFont: PDFont,
  bodyFont: PDFont,
  fontSize: Float,
  title: String
) extends AbstractTable[JpRosterData](
  roster,
  new MultiDrawableTable(List(DrawableTable(
    List(List(title)),
    List(JpClassRosterView.getWidths(data).sum),
    List(ALIGN_CENTER),
    headerFont,
    fontSize,
    1,
    3
  ), DrawableTable(
    List(JpClassRosterView.getHeaderTexts(data)),
    JpClassRosterView.getWidths(data),
    JpClassRosterView.getHeaderAligns(data),
    headerFont,
    fontSize,
    1,
    3
  ))),
  MultiDrawableTable.empty,
  JpClassRosterView.getBodyAligns(data),
  bodyFont,
  fontSize,
  1,
  6
)

object JpClassRosterView {
  def getHeaderTexts(data: JpClassRosterModel): List[String] = "Name" :: ((1 to data.instanceData.numberSessions).map("Day " + _) :+ "Pass" ).toList
  def getWidths(data: JpClassRosterModel): List[Float] = 270f :: (1 to data.instanceData.numberSessions+1).map(_ => 40f).toList
  def getHeaderAligns(data: JpClassRosterModel): List[Alignment] = (1 to data.instanceData.numberSessions+2).map(_ => ALIGN_CENTER).toList
  def getBodyAligns(data: JpClassRosterModel): List[Alignment] = (1 to data.instanceData.numberSessions+2).map(_ => ALIGN_LEFT).toList
}