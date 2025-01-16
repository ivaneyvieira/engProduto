package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.NotaVendaRef
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.CENTER
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation
import net.sf.dynamicreports.report.constant.TextAdjust

class ReportVendaRef : ReportBuild<NotaVendaRef>() {
  init {
    columnReport(NotaVendaRef::loja, header = "Loja", width = 30, aligment = CENTER)
    columnReport(NotaVendaRef::pdv, header = "PDV", width = 30, aligment = CENTER)
    columnReport(NotaVendaRef::data, header = "Data", width = 55)
    columnReport(NotaVendaRef::nota, header = "NF", width = 50, aligment = RIGHT)
    columnReport(NotaVendaRef::uf, header = "UF", width = 20, aligment = CENTER)
    columnReport(NotaVendaRef::numeroInterno, header = "NI", width = 70)
    columnReport(NotaVendaRef::tipoPgto, header = "Tipo Pgto", width = 70) {
      this.setTextAdjust(TextAdjust.SCALE_FONT)
    }
    columnReport(NotaVendaRef::valor, header = "Valor NF", width = 40)
    columnReport(NotaVendaRef::valorTipo, header = "Valor TP", width = 40)
    columnReport(NotaVendaRef::cliente, header = "Cód Cli", pattern = "0", width = 40)
    columnReport(NotaVendaRef::nomeCliente, header = "Nome Cliente") {
      this.setTextAdjust(TextAdjust.CUT_TEXT)
    }
  }

  override fun config(itens: List<NotaVendaRef>): PropriedadeRelatorio {
    return PropriedadeRelatorio(
      titulo = "Vendas",
      subTitulo = "",
      detailFonteSize = 8,
      pageOrientation = PageOrientation.PORTRAIT,
      margem = 10
    )
  }
}