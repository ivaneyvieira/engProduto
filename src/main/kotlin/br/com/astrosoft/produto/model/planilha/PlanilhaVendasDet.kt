package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaVendaDet

class PlanilhaVendasDet : Planilha<NotaVendaDet>("Vendas") {
  init {
    columnSheet(NotaVendaDet::loja, header = "Loja")
    columnSheet(NotaVendaDet::pedido, header = "Pedido")
    columnSheet(NotaVendaDet::pdv, header = "PDV")
    columnSheet(NotaVendaDet::data, header = "Data")
    columnSheet(NotaVendaDet::transacao, header = "Transação")
    columnSheet(NotaVendaDet::nota, header = "NF")
    columnSheet(NotaVendaDet::uf, header = "UF")
    columnSheet(NotaVendaDet::tipoNf, header = "Tipo NF")
    columnSheet(NotaVendaDet::hora, header = "Hora")
    columnSheet(NotaVendaDet::numeroInterno, header = "NI")
    columnSheet(NotaVendaDet::numMetodo, header = "Met")
    columnSheet(NotaVendaDet::nomeMetodo, header = "Nome Met")
    columnSheet(NotaVendaDet::mult, pattern = "#,##0.0000", header = "Mlt")
    columnSheet(NotaVendaDet::valor, header = "Valor NF")
    columnSheet(NotaVendaDet::cliente, header = "Cód Cli")
    columnSheet(NotaVendaDet::nomeCliente, header = "Nome Cliente")
    columnSheet(NotaVendaDet::vendedor, header = "Vendedor")
  }
}