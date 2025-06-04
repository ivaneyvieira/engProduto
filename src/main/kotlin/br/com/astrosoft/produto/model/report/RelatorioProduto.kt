package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports.margin
import net.sf.dynamicreports.report.builder.DynamicReports.stl
import net.sf.dynamicreports.report.builder.style.Styles
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.*
import net.sf.dynamicreports.report.constant.PageOrientation
import java.awt.Color

class RelatorioProduto(val lojaEstoque: Int) : ReportBuild<ProdutoRelatorio>() {
  init {
    columnReport(ProdutoRelatorio::codigo, header = "Código", aligment = RIGHT, width = 80)
    columnReport(ProdutoRelatorio::descricao, header = "Descrição", aligment = LEFT)
    columnReport(ProdutoRelatorio::grade, header = "Grade", aligment = CENTER, width = 100)
    columnReport(ProdutoRelatorio::unidade, header = "Unidade", aligment = CENTER, width = 80)
    columnReport(ProdutoRelatorio::quant, header = "Quant", aligment = RIGHT, width = 80)
  }

  override fun makeReport(itens: List<ProdutoRelatorio>): JasperReportBuilder {
    return super
      .makeReport(itens)
      .setPageMargin(margin(0))
      .setColumnHeaderStyle(
        stl.style().setForegroundColor(Color.WHITE).setLeftPadding(8).setRightPadding(8)
      )
      .setTitleStyle(
        stl.style().setForegroundColor(Color.WHITE).setPadding(Styles.padding().setTop(20))
      )
      .setColumnStyle(
        stl.style().setForegroundColor(Color.WHITE).setLeftPadding(8).setRightPadding(8)
      )
      .setGroupStyle(stl.style().setForegroundColor(Color.WHITE).setPadding(Styles.padding().setLeft(8)))
      .setBackgroundStyle(stl.style().setBackgroundColor(Color(35, 51, 72)))
  }

  override fun config(itens: List<ProdutoRelatorio>): PropriedadeRelatorio {
    val loja = when (lojaEstoque) {
      0    -> "Todas"
      2    -> "DS"
      3    -> "MR"
      4    -> "MF"
      5    -> "PK"
      8    -> "TM"
      else -> "Outra"
    }
    return PropriedadeRelatorio(
      titulo = "Estoque Loja $loja",
      subTitulo = "",
      color = Color.WHITE,
      detailFonteSize = 14,
      pageOrientation = PageOrientation.PORTRAIT
    )
  }
}

data class ProdutoRelatorio(
  val codigo: Int,
  val descricao: String,
  val grade: String,
  val unidade: String,
  val quant: Int
)