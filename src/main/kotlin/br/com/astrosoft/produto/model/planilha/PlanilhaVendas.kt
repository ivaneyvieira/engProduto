package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaVenda

class PlanilhaVendas : Planilha<NotaVenda>("Vendas") {
  init {
    columnSheet(NotaVenda::loja, header = "Loja")
    columnSheet(NotaVenda::pedido, header = "Pedido", pattern = "0")
    columnSheet(NotaVenda::pdv, header = "PDV")
    columnSheet(NotaVenda::data, header = "Data")
    columnSheet(NotaVenda::transacao, header = "Transação", pattern = "0")
    columnSheet(NotaVenda::nota, header = "NF")
    columnSheet(NotaVenda::uf, header = "UF")
    columnSheet(NotaVenda::tipoNf, header = "Tipo NF")
    columnSheet(NotaVenda::hora, header = "Hora")
    columnSheet(NotaVenda::numeroInterno, header = "NI")
    columnSheet(NotaVenda::tipoPgto, header = "Tipo Pgto")
    columnSheet(NotaVenda::valor, header = "Valor NF")
    columnSheet(NotaVenda::valorTipo, header = "Valor TP")
    columnSheet(NotaVenda::cliente, header = "Cód Cli", pattern = "0")
    columnSheet(NotaVenda::nomeCliente, header = "Nome Cliente")
    columnSheet(NotaVenda::vendedor, header = "Vendedor")
  }
}