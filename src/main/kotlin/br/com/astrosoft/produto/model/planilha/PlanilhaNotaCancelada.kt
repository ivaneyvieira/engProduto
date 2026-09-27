package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaSolicitaCancelar

class PlanilhaNotaCancelada : Planilha<NotaSolicitaCancelar>("Vendas") {
  init {
    columnSheet(NotaSolicitaCancelar::loja, header = "Loja")
    columnSheet(NotaSolicitaCancelar::pedido, header = "Pedido")
    columnSheet(NotaSolicitaCancelar::pdv, header = "PDV")
    columnSheet(NotaSolicitaCancelar::data, header = "Data")
    columnSheet(NotaSolicitaCancelar::nota, header = "NF")
    columnSheet(NotaSolicitaCancelar::motivoDescricao, header = "Motivo")
    columnSheet(NotaSolicitaCancelar::loginCancel, header = "Autorizado")
    columnSheet(NotaSolicitaCancelar::uf, header = "UF")
    columnSheet(NotaSolicitaCancelar::tipoNotaSaida, header = "Tipo NF")
    columnSheet(NotaSolicitaCancelar::hora, header = "Hora")
    columnSheet(NotaSolicitaCancelar::numMetodo, header = "Met")
    columnSheet(NotaSolicitaCancelar::nomeMetodo, header = "Nome Met")
    columnSheet(NotaSolicitaCancelar::documento, header = "Documento")
    columnSheet(NotaSolicitaCancelar::tipoPgto, header = "Tipo Pgto")
    columnSheet(NotaSolicitaCancelar::valor, header = "Valor NF")
    columnSheet(NotaSolicitaCancelar::cliente, header = "Cód Cli")
    columnSheet(NotaSolicitaCancelar::nomeCliente, header = "Nome Cliente")
    columnSheet(NotaSolicitaCancelar::vendedor, header = "Vendedor")
  }
}