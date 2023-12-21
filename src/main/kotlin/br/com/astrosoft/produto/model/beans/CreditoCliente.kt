package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class CreditoCliente(
  var codigo: Int,
  var nome: String,
  var vlCredito: Double,
  var loja: Int?,
  var tipo: String?,
) {
  companion object {
    fun findCreditoCliente(filtro: FiltroCreditoCliente): List<CreditoCliente> {
      return saci.findCreditoCliente(filtro)
    }
  }
}

data class FiltroCreditoCliente(
  val pesquisa: String,
)