package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaVenda
import br.com.astrosoft.produto.model.beans.NotaVendaRef

class PlanilhaVendasRef : Planilha<NotaVendaRef>("Vendas") {
  init {
    columnSheet(NotaVendaRef::loja, header = "Loja")
    columnSheet(NotaVendaRef::pedido, header = "Pedido", pattern = "0")
    columnSheet(NotaVendaRef::pdv, header = "PDV")
    columnSheet(NotaVendaRef::data, header = "Data")
    columnSheet(NotaVendaRef::transacao, header = "Transação", pattern = "0")
    columnSheet(NotaVendaRef::nota, header = "NF")
    columnSheet(NotaVendaRef::uf, header = "UF")
    columnSheet(NotaVendaRef::tipoNf, header = "Tipo NF")
    columnSheet(NotaVendaRef::hora, header = "Hora")
    columnSheet(NotaVendaRef::numeroInterno, header = "NI")
    columnSheet(NotaVendaRef::tipoPgto, header = "Tipo Pgto")
    columnSheet(NotaVendaRef::valor, header = "Valor NF")
    columnSheet(NotaVendaRef::valorTipo, header = "Valor TP")
    columnSheet(NotaVendaRef::cliente, header = "Cód Cli", pattern = "0")
    columnSheet(NotaVendaRef::nomeCliente, header = "Nome Cliente")
    columnSheet(NotaVendaRef::vendedor, header = "Vendedor")
  }
}