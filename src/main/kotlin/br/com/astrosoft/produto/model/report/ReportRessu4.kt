package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.ProdutoTransfRessu4
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.*
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.CENTER
import net.sf.dynamicreports.report.constant.PageOrientation
import net.sf.dynamicreports.report.constant.TextAdjust

class ReportRessu4() : ReportBuild<ProdutoTransfRessu4>() {
  init {
    columnReport(ProdutoTransfRessu4::codigo, "Código", width = 45, aligment = RIGHT)
    columnReport(ProdutoTransfRessu4::descricao, "Descrição").scaleFont()
    columnReport(ProdutoTransfRessu4::grade, "Grade", width = 65, aligment = CENTER)
    columnReport(ProdutoTransfRessu4::codigoBarras, "Código de Barras", width = 85, aligment = RIGHT).scaleFont()
    columnReport(ProdutoTransfRessu4::referencia, "Ref Fornecedor", width = 100, aligment = LEFT).scaleFont()
    columnReport(ProdutoTransfRessu4::quant, "Quant", pattern = "#,##0", width = 35)
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