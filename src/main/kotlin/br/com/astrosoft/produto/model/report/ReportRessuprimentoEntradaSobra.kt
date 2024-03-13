package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimentoSobra
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports
import net.sf.dynamicreports.report.builder.DynamicReports.grid
import net.sf.dynamicreports.report.builder.style.Styles
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment
import net.sf.dynamicreports.report.constant.PageOrientation
import java.awt.Color

class ReportRessuprimentoEntradaSobra(private val ressuprimentoTitle: String) :
  ReportBuild<ProdutoRessuprimentoSobra>() {

  init {

    val grupoDados = grid.titleGroup(
      "Falta",
      columnReport(ProdutoRessuprimentoSobra::codigo, "Código", width = 40, aligment = HorizontalTextAlignment.RIGHT),
      columnReport(ProdutoRessuprimentoSobra::descricao, "Descrição") {
        this.scaleFont()
      },
      columnReport(ProdutoRessuprimentoSobra::grade, "Grade", width = 50) {
        this.scaleFont()
      },
      columnReport(ProdutoRessuprimentoSobra::nota, "Nota", width = 50) {
        this.scaleFont()
      },
      columnReport(ProdutoRessuprimentoSobra::quantidade, "Quant", width = 35)
    )

    val espaco = grid.titleGroup("",
      columnReport(ProdutoRessuprimentoSobra::espaco, " ", width = 30)
    )

    val grupoRecebimento = grid.titleGroup(
      "Sobra",
      columnReport(
        ProdutoRessuprimentoSobra::codigoSobra,
        "Código",
        width = 45,
        aligment = HorizontalTextAlignment.RIGHT
      ),
      columnReport(ProdutoRessuprimentoSobra::descricaoSobra, "Descrição") {
        this.scaleFont()
      },
      columnReport(ProdutoRessuprimentoSobra::gradeSobra, "Grade", width = 50) {
        this.scaleFont()
      },
      columnReport(ProdutoRessuprimentoSobra::quantidadeSobra, "Quant", width = 35)
    )

    addGrupo(grupoDados)
    addGrupo(espaco)
    addGrupo(grupoRecebimento)
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
      titulo = "Relatório com Divergência no Recebimento do Ressuprimento da $ressuprimentoTitle",
      subTitulo = "",
      detailFonteSize = 8,
      color = Color.WHITE,
      pageOrientation = PageOrientation.PORTRAIT,
      margem = 10
    )
  }
}