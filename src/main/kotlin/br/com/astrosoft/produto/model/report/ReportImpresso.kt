package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import net.sf.dynamicreports.report.constant.PageOrientation

class ReportImpresso : ReportBuild<EntradaDevCli>() {
  init {
    columnReport(EntradaDevCli::loja, header = "Loja")
    columnReport(EntradaDevCli::fezTrocaCol, header = "Troca")
    columnReport(EntradaDevCli::invno, header = "NI")
    columnReport(EntradaDevCli::notaFiscal, header = "NF Dev")
    columnReport(EntradaDevCli::data, header = "Data")
    columnReport(EntradaDevCli::valor, header = "Valor Dev")
    columnReport(EntradaDevCli::observacao02, header = "Tipo")
    columnReport(EntradaDevCli::nfVenda, header = "NF Venda")
    columnReport(EntradaDevCli::nfData, header = "Data")
    columnReport(EntradaDevCli::custno, header = "Cód Cli")
    columnReport(EntradaDevCli::cliente, header = "Nome do Cliente")
  }

  override fun config(itens: List<EntradaDevCli>): PropriedadeRelatorio {
    return PropriedadeRelatorio(
      titulo = "Pedidos",
      subTitulo = "",
      detailFonteSize = 8,
      pageOrientation = PageOrientation.PORTRAIT
    )
  }
}