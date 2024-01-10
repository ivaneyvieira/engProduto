package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports
import net.sf.dynamicreports.report.builder.DynamicReports.margin
import net.sf.dynamicreports.report.builder.DynamicReports.stl
import net.sf.dynamicreports.report.builder.style.Styles
import net.sf.dynamicreports.report.builder.style.Styles.padding
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.CENTER
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation
import java.awt.Color

class ReportImpresso : ReportBuild<EntradaDevCli>() {
  init {
    columnReport(EntradaDevCli::loja, header = "Loja", aligment = CENTER, width = 30)
    columnReport(EntradaDevCli::fezTrocaCol, header = "Troca", aligment = CENTER, width = 30)
    columnReport(EntradaDevCli::invno, header = "NI", aligment = CENTER)
    columnReport(EntradaDevCli::notaFiscal, header = "NF Dev", aligment = CENTER)
    columnReport(EntradaDevCli::data, header = "Data", aligment = CENTER)
    columnReport(EntradaDevCli::valor, header = "Valor Dev")
    columnReport(EntradaDevCli::observacao02, header = "Tipo", aligment = CENTER, width = 70)
    columnReport(EntradaDevCli::nfVenda, header = "NF Venda", aligment = CENTER)
    columnReport(EntradaDevCli::nfData, header = "Data", aligment = CENTER)
    columnReport(EntradaDevCli::custno, header = "Cód Cli", aligment = RIGHT)
    columnReport(EntradaDevCli::cliente, header = "Nome do Cliente", width = 200)
  }

  override fun makeReport(itens: List<EntradaDevCli>): JasperReportBuilder {
    return super
      .makeReport(itens)
      .setPageMargin(margin(0))
      .setTitleStyle(stl.style().setForegroundColor(Color.WHITE).setPadding(padding().setTop(20)))
      .setColumnStyle(stl.style().setForegroundColor(Color.WHITE).setFontSize(8).setLeftPadding(3).setRightPadding(3))
      .setGroupStyle(stl.style().setForegroundColor(Color.WHITE).setPadding(padding().setLeft(4)))
      .setBackgroundStyle(stl.style().setBackgroundColor(Color(35, 51, 72)))
  }

  override fun config(itens: List<EntradaDevCli>): PropriedadeRelatorio {
    return PropriedadeRelatorio(
      titulo = "Pedidos",
      subTitulo = "",
      color = Color.WHITE,
      detailFonteSize = 8,
      pageOrientation = PageOrientation.LANDSCAPE
    )
  }
}