package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.*
import net.sf.dynamicreports.report.constant.PageOrientation
import java.awt.Color

class ReportAcerto() : ReportBuild<ProdutoEstoqueAcerto>() {
  init {
    columnReport(ProdutoEstoqueAcerto::codigo, header = "Código", aligment = RIGHT, width = 40)
    columnReport(ProdutoEstoqueAcerto::descricao, header = "Descrição", aligment = LEFT)
    columnReport(ProdutoEstoqueAcerto::grade, header = "Grade", aligment = LEFT, width = 40)
    columnReport(ProdutoEstoqueAcerto::estoqueSis, header = "Est Sist", aligment = RIGHT, width = 60)
    columnReport(ProdutoEstoqueAcerto::estoqueCD, header = "Est CD", aligment = RIGHT, width = 60)
    columnReport(ProdutoEstoqueAcerto::estoqueLoja, header = "Est Loja", aligment = RIGHT, width = 60)
    columnReport(ProdutoEstoqueAcerto::diferenca, header = "Diferença", aligment = RIGHT, width = 60)
  }

  override fun makeReport(itens: List<ProdutoEstoqueAcerto>): JasperReportBuilder {
    return super
      .makeReport(itens)
      //.setPageMargin(margin(0))
//      .setTitleStyle(stl.style().setForegroundColor(Color.WHITE).setPadding(padding().setTop(20)))
//      .setColumnStyle(stl.style().setForegroundColor(Color.WHITE).setFontSize(8).setLeftPadding(3).setRightPadding(3))
//      .setGroupStyle(stl.style().setForegroundColor(Color.WHITE).setPadding(padding().setLeft(4)))
    //    .setBackgroundStyle(stl.style().setBackgroundColor(Color(35, 51, 72)))
  }

  override fun config(itens: List<ProdutoEstoqueAcerto>): PropriedadeRelatorio {
    val acerto = itens.firstOrNull()
    val numero = acerto?.numero
    val loja = acerto?.estoqueLoja

    return PropriedadeRelatorio(
      titulo = "Acerto de Estoque",
      subTitulo = "Produtos do Acerto $numero - Loja $loja",
      color = Color.BLACK,
      detailFonteSize = 8,
      pageOrientation = PageOrientation.PORTRAIT
    )
  }
}