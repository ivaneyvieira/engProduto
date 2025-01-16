package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimentoSobra
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder
import net.sf.dynamicreports.report.builder.style.Styles
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment
import net.sf.dynamicreports.report.constant.PageOrientation
import java.awt.Color

class ReportRessuprimentoEntradaSobra(private val ressuprimentoTitle: String) :
  ReportBuild<ProdutoRessuprimentoSobra>() {

  init {
    columnReport(ProdutoRessuprimentoSobra::codigo, "Código", width = 45, aligment = HorizontalTextAlignment.RIGHT)
    columnReport(ProdutoRessuprimentoSobra::descricao, "Descrição", width = 200)
    columnReport(ProdutoRessuprimentoSobra::localizacao, "Loc", width = 45)
    columnReport(ProdutoRessuprimentoSobra::grade, "Grade", width = 60) {
      this.scaleFont()
    }
    columnReport(ProdutoRessuprimentoSobra::nota, "Nota", width = 60) {
      this.scaleFont()
    }
    columnReport(ProdutoRessuprimentoSobra::quantidade, "Quant", width = 40)
  }

  override fun labelTitleCol(): TextColumnBuilder<String> {
    return columnReport(ProdutoRessuprimentoSobra::grupo, "Grupo") {
      this.scaleFont()
    }
  }

  override fun makeReport(itens: List<ProdutoRessuprimentoSobra>): JasperReportBuilder {
    return super
      .makeReport(itens)
      .setPageMargin(DynamicReports.margin(0))
      .setTitleStyle(DynamicReports.stl.style().setForegroundColor(Color.WHITE).setPadding(Styles.padding().setTop(20)))
      .setColumnStyle(
        DynamicReports.stl.style().setForegroundColor(Color.WHITE).setFontSize(8).setLeftPadding(3).setRightPadding(3)
      )
      .setGroupStyle(DynamicReports.stl.style().setForegroundColor(Color.WHITE).setPadding(Styles.padding().setLeft(4)))
      .setBackgroundStyle(DynamicReports.stl.style().setBackgroundColor(Color(35, 51, 72)))
  }

  override fun config(itens: List<ProdutoRessuprimentoSobra>): PropriedadeRelatorio {
    return PropriedadeRelatorio(
      titulo = "Divergência no Recebimento do Ressuprimento da $ressuprimentoTitle",
      tituloAlin = HorizontalTextAlignment.CENTER,
      tituloLargura = 445,
      subTitulo = "",
      detailFonteSize = 8,
      color = Color.WHITE,
      pageOrientation = PageOrientation.PORTRAIT,
      margem = 10
    )
  }
}