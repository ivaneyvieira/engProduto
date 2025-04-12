package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueGarantia
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.*
import net.sf.dynamicreports.report.constant.PageOrientation
import java.awt.Color

class ReportGarantia() : ReportBuild<ProdutoEstoqueGarantia>() {
  init {
    columnReport(ProdutoEstoqueGarantia::codigo, header = "Código", aligment = RIGHT, width = 40)
    columnReport(ProdutoEstoqueGarantia::descricao, header = "Descrição", aligment = LEFT)
    columnReport(ProdutoEstoqueGarantia::grade, header = "Grade", aligment = LEFT, width = 40)
    columnReport(ProdutoEstoqueGarantia::estoqueSis, header = "Est Loja", aligment = RIGHT, width = 60)
    columnReport(ProdutoEstoqueGarantia::estoqueLoja, header = "Est Lojas", aligment = RIGHT, width = 60)
    columnReport(ProdutoEstoqueGarantia::estoqueReal, header = "Est Dif", aligment = RIGHT, width = 60)
  }

  override fun makeReport(itens: List<ProdutoEstoqueGarantia>): JasperReportBuilder {
    return super
      .makeReport(itens)
  }

  override fun config(itens: List<ProdutoEstoqueGarantia>): PropriedadeRelatorio {
    val Garantia = itens.firstOrNull()
    val numero = Garantia?.numero
    val loja = Garantia?.estoqueLoja

    return PropriedadeRelatorio(
      titulo = "Garantia de Estoque",
      subTitulo = "Produtos do Garantia $numero - Loja $loja",
      color = Color.BLACK,
      detailFonteSize = 8,
      pageOrientation = PageOrientation.PORTRAIT
    )
  }
}