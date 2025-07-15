package br.com.astrosoft.framework.model.reports

import net.sf.dynamicreports.report.builder.DynamicReports.stl
import net.sf.dynamicreports.report.builder.DynamicReports.template
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder
import net.sf.dynamicreports.report.builder.style.StyleBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.CENTER
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.LEFT
import net.sf.dynamicreports.report.constant.PageOrientation.LANDSCAPE
import net.sf.dynamicreports.report.constant.PageType.A4
import net.sf.dynamicreports.report.constant.VerticalTextAlignment.MIDDLE
import java.awt.Color

object Templates {
  val rootStyle: StyleBuilder = stl.style().setPadding(1)
  val boldStyle: StyleBuilder = stl.style(rootStyle).bold()
  val italicStyle: StyleBuilder = stl.style(rootStyle).italic()
  val boldCenteredStyle: StyleBuilder = stl.style(boldStyle).setTextAlignment(CENTER, MIDDLE)
  val bold9CenteredStyle: StyleBuilder = stl.style(boldCenteredStyle).setFontSize(9)
  val columnStyle: StyleBuilder = stl.style(rootStyle).setFontSize(8)
  val columnTitleStyle: StyleBuilder =
      stl
        .style(columnStyle)
        .setBorder(stl.pen1Point())
        .setHorizontalTextAlignment(CENTER)
        .setBackgroundColor(Color.LIGHT_GRAY)
        .bold()
  val groupStyle: StyleBuilder = stl.style(boldStyle).setHorizontalTextAlignment(LEFT)
  val subtotalStyle: StyleBuilder = stl.style(boldStyle)
  val reportTemplate: ReportTemplateBuilder =
      template()
        .setPageFormat(A4, LANDSCAPE)
        .setColumnStyle(columnStyle)
        .setColumnTitleStyle(columnTitleStyle)
        .setGroupStyle(groupStyle)
        .setGroupTitleStyle(groupStyle)
        .setSubtotalStyle(subtotalStyle)
        .setDetailStyle(stl.style(rootStyle).setFontSize(8))
  val fieldFontTitle: StyleBuilder = stl.style(rootStyle).setFontSize(4)
  val fieldFont: StyleBuilder = stl.style(rootStyle).setFontSize(6)
  val fieldBorder: StyleBuilder = stl.style(fieldFont).setBorder(stl.penThin()).setRadius(10)
  val fieldFontNormal: StyleBuilder = stl.style(rootStyle).setFontSize(10)
  val fieldFontNormalCol: StyleBuilder =
      stl
        .style(fieldFontNormal)
        .setBorder(stl.pen1Point())
        .setHorizontalTextAlignment(CENTER)
        .setBackgroundColor(Color.LIGHT_GRAY)
        .bold()
  val fieldFontGrande: StyleBuilder = stl.style(rootStyle).setFontSize(10)

  val rootTexto = stl.style().setPadding(1).setFontName("SansSerif")
  val fieldFontTermo: StyleBuilder = stl.style(rootTexto).setFontSize(10)
  val fieldFontQuadro : StyleBuilder = stl.style(rootTexto).setFontSize(8).setBorder(stl.penThin()).setPadding(3)
  val fieldFontTituloQuadro : StyleBuilder = stl.style(rootTexto).setFontSize(10).setBorder(stl.penThin()).setPadding(3).bold()
  val fieldFontTitulo: StyleBuilder = stl.style(rootTexto).setFontSize(12).bold()
}