package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.NotaVenda
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.CENTER
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation
import net.sf.dynamicreports.report.constant.TextAdjust

class ReportVenda : ReportBuild<NotaVenda>() {
  init {
    columnReport(NotaVenda::loja, header = "Loja", width = 30, aligment = CENTER)
    columnReport(NotaVenda::pdv, header = "PDV", width = 30, aligment = CENTER)
    columnReport(NotaVenda::data, header = "Data", width = 55)
    columnReport(NotaVenda::nota, header = "NF", width = 50, aligment = RIGHT)
    columnReport(NotaVenda::uf, header = "UF", width = 20, aligment = CENTER)
    columnReport(NotaVenda::numeroInterno, header = "NI", width = 70)
    columnReport(NotaVenda::tipoPgto, header = "Tipo Pgto", width = 70){
      this.setTextAdjust(TextAdjust.SCALE_FONT)
    }
    columnReport(NotaVenda::valor, header = "Valor NF", width = 40)
    columnReport(NotaVenda::valorTipo, header = "Valor TP", width = 40)
    columnReport(NotaVenda::cliente, header = "Cód Cli", pattern = "0", width = 40)
    columnReport(NotaVenda::nomeCliente, header = "Nome Cliente"){
      this.setTextAdjust(TextAdjust.CUT_TEXT)
    }
  }

  override fun config(itens: List<NotaVenda>): PropriedadeRelatorio {
    return PropriedadeRelatorio(
      titulo = "Vendas",
      subTitulo = "",
      detailFonteSize = 8,
      pageOrientation = PageOrientation.PORTRAIT,
      margem = 10
    )
  }
}