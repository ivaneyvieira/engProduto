package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.NotaSolicitaCancelar
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.CENTER
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation
import net.sf.dynamicreports.report.constant.TextAdjust

class ReportNotaCancelada : ReportBuild<NotaSolicitaCancelar>() {
  init {
    columnReport(NotaSolicitaCancelar::loja, header = "Loja", width = 30, aligment = CENTER)
    columnReport(NotaSolicitaCancelar::pdv, header = "PDV", width = 30, aligment = CENTER)
    columnReport(NotaSolicitaCancelar::data, header = "Data", width = 55)
    columnReport(NotaSolicitaCancelar::nota, header = "NF", width = 50, aligment = RIGHT)
    columnReport(NotaSolicitaCancelar::uf, header = "UF", width = 20, aligment = CENTER)
    columnReport(NotaSolicitaCancelar::numeroInterno, header = "NI", width = 70)
    columnReport(NotaSolicitaCancelar::tipoPgto, header = "Tipo Pgto", width = 70) {
      this.setTextAdjust(TextAdjust.SCALE_FONT)
    }
    columnReport(NotaSolicitaCancelar::valor, header = "Valor NF", width = 40)
    columnReport(NotaSolicitaCancelar::valorTipo, header = "Valor TP", width = 40)
    columnReport(NotaSolicitaCancelar::cliente, header = "Cód Cli", pattern = "0", width = 40)
    columnReport(NotaSolicitaCancelar::nomeCliente, header = "Nome Cliente") {
      this.setTextAdjust(TextAdjust.CUT_TEXT)
    }
  }
  
  override fun config(itens: List<NotaSolicitaCancelar>): PropriedadeRelatorio {
    return PropriedadeRelatorio(
      titulo = "Vendas", subTitulo = "", detailFonteSize = 8, pageOrientation = PageOrientation.PORTRAIT, margem = 10
    )
  }
}