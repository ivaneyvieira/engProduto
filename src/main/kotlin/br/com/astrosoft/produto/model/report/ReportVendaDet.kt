package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.NotaVendaDet
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.CENTER
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation
import net.sf.dynamicreports.report.constant.TextAdjust

class ReportVendaDet : ReportBuild<NotaVendaDet>() {
  init {
    columnReport(NotaVendaDet::loja, header = "Loja", width = 30, aligment = CENTER)
    columnReport(NotaVendaDet::pdv, header = "PDV", width = 30, aligment = CENTER)
    columnReport(NotaVendaDet::data, header = "Data", width = 55)
    columnReport(NotaVendaDet::nota, header = "NF", width = 50, aligment = RIGHT)
    columnReport(NotaVendaDet::uf, header = "UF", width = 20, aligment = CENTER)
    columnReport(NotaVendaDet::numeroInterno, header = "NI", width = 70)
    columnReport(NotaVendaDet::valor, header = "Valor NF", width = 40)
    columnReport(NotaVendaDet::cliente, header = "Cód Cli", pattern = "0", width = 40)
    columnReport(NotaVendaDet::nomeCliente, header = "Nome Cliente") {
      this.setTextAdjust(TextAdjust.CUT_TEXT)
    }
  }

  override fun config(itens: List<NotaVendaDet>): PropriedadeRelatorio {
    return PropriedadeRelatorio(
      titulo = "Vendas",
      subTitulo = "",
      detailFonteSize = 8,
      pageOrientation = PageOrientation.PORTRAIT,
      margem = 10
    )
  }
}