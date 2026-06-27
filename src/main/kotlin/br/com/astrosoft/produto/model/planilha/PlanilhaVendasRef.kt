package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaVendaRef

class PlanilhaVendasRef : Planilha<NotaVendaRef>("Vendas") {
  init {
    columnSheet(NotaVendaRef::loja, header = "Loja")
    columnSheet(NotaVendaRef::pedido, header = "Pedido")
    columnSheet(NotaVendaRef::pdv, header = "PDV")
    columnSheet(NotaVendaRef::data, header = "Data")
    columnSheet(NotaVendaRef::transacao, header = "Transação")
    columnSheet(NotaVendaRef::nota, header = "NF")
    columnSheet(NotaVendaRef::uf, header = "UF")
    columnSheet(NotaVendaRef::tipoNf, header = "Tipo NF")
    columnSheet(NotaVendaRef::hora, header = "Hora")
    columnSheet(NotaVendaRef::numeroInterno, header = "NI")
    columnSheet(NotaVendaRef::numMetodo, header = "Met")
    columnSheet(NotaVendaRef::nomeMetodo, header = "Nome Met")
    columnSheet(NotaVendaRef::mult, pattern = "#,##0.0000", header = "Mlt")
    columnSheet(NotaVendaRef::documento, header = "Documento")
    columnSheet(NotaVendaRef::mediaPrazo, header = "Pz M")
    columnSheet(NotaVendaRef::tipoPgto, header = "Tipo Pgto")
    columnSheet(NotaVendaRef::valor, header = "Valor NF")
    columnSheet(NotaVendaRef::valorTipo, header = "Valor TP")
    columnSheet(NotaVendaRef::cliente, header = "Cód Cli")
    columnSheet(NotaVendaRef::nomeCliente, header = "Nome Cliente")
    columnSheet(NotaVendaRef::vendedor, header = "Vendedor")
  }
}