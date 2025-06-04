package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.ProdutoPedidoGarantia
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.LEFT
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation
import java.awt.Color

class ReportGarantia() : ReportBuild<ProdutoPedidoGarantia>() {
  init {
    columnReport(ProdutoPedidoGarantia::codigo, header = "Código", aligment = RIGHT, width = 40)
    columnReport(ProdutoPedidoGarantia::descricao, header = "Descrição", aligment = LEFT)
    columnReport(ProdutoPedidoGarantia::grade, header = "Grade", aligment = LEFT, width = 40)
    columnReport(ProdutoPedidoGarantia::estoqueLoja, header = "Est Loja", aligment = RIGHT, width = 60)
    columnReport(ProdutoPedidoGarantia::estoqueLojas, header = "Est Lojas", aligment = RIGHT, width = 60)
    columnReport(ProdutoPedidoGarantia::estoqueDev, header = "Est Dev", aligment = RIGHT, width = 60)
  }

  override fun makeReport(itens: List<ProdutoPedidoGarantia>): JasperReportBuilder {
    return super
      .makeReport(itens)
  }

  override fun config(itens: List<ProdutoPedidoGarantia>): PropriedadeRelatorio {
    val Garantia = itens.firstOrNull()
    val numero = Garantia?.numero
    val loja = Garantia?.estoqueLojas

    return PropriedadeRelatorio(
      titulo = "Garantia de Estoque",
      subTitulo = "Produtos do Garantia $numero - Loja $loja",
      color = Color.BLACK,
      detailFonteSize = 8,
      pageOrientation = PageOrientation.PORTRAIT
    )
  }
}