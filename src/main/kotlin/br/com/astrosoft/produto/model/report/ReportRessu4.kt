package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.ProdutoTransfRessu4
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.CENTER
import net.sf.dynamicreports.report.constant.PageOrientation

class ReportRessu4() : ReportBuild<ProdutoTransfRessu4>() {
  init {
    columnReport(ProdutoTransfRessu4::codigo, "Código", width = 40)
    columnReport(ProdutoTransfRessu4::descricao, "Descrição")
    columnReport(ProdutoTransfRessu4::grade, "Grade", width = 65, aligment = CENTER)
    columnReport(ProdutoTransfRessu4::codigoBarras, "Código de Barras", width = 85, aligment = CENTER)
    columnReport(ProdutoTransfRessu4::referencia, "Ref Fornecedor", width = 100, aligment = CENTER)
    columnReport(ProdutoTransfRessu4::quant, "Quant", pattern = "#,##0", width = 30)
  }

  override fun config(itens: List<ProdutoTransfRessu4>): PropriedadeRelatorio {
    return PropriedadeRelatorio(
      titulo = "NF Transf ${itens.firstOrNull()?.notaTransf} - ${itens.firstOrNull()?.rota}",
      subTitulo = "",
      detailFonteSize = 10,
      pageOrientation = PageOrientation.PORTRAIT
    )
  }
}