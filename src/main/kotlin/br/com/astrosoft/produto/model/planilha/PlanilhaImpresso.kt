package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.EntradaDevCli

class PlanilhaImpresso : Planilha<EntradaDevCli>("Credito não Utilizado") {
  init {
    columnSheet(EntradaDevCli::loja, header = "Loja")
    columnSheet(EntradaDevCli::fezTrocaCol, header = "Troca")
    columnSheet(EntradaDevCli::invno, header = "NI")
    columnSheet(EntradaDevCli::notaFiscal, header = "NF Dev")
    columnSheet(EntradaDevCli::data, header = "Data")
    columnSheet(EntradaDevCli::vendno, header = "Cód For")
    columnSheet(EntradaDevCli::fornecedor, header = "Fornecedor")
    columnSheet(EntradaDevCli::valor, header = "Valor Dev")
    columnSheet(EntradaDevCli::observacao, header = "Observação")
    columnSheet(EntradaDevCli::tipoObs, header = "Tipo")
    columnSheet(EntradaDevCli::nfVenda, header = "NF Venda")
    columnSheet(EntradaDevCli::nfData, header = "Data")
    columnSheet(EntradaDevCli::custno, header = "Cód Cli")
    columnSheet(EntradaDevCli::cliente, header = "Nome do Cliente")
    columnSheet(EntradaDevCli::nfValor, header = "Valor Venda")
    columnSheet(EntradaDevCli::impressora, header = "Impressora")
    columnSheet(EntradaDevCli::userName, header = "Usuário")
  }
}