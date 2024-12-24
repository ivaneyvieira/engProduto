package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class CreditoCliente(
  var codigo: Int?,
  var nome: String?,
  var vlCredito: Double?,
  var invno: Int?,
  var loja: Int?,
  var tipo: String?,
  var ni: Int?,
  var nfDev: String?,
  var dtDev: LocalDate?,
  var vendno: Int?,
  var fornecedor: String?,
  var valorDev: Double?,
  var nfVenda: String?,
  var dtVenda: LocalDate?,
  var custnoVenda: Int?,
  var clienteVenda: String?,
  var remarks: String?,
  var valorVenda: Double?,
) {
  fun produtos() = saci.entradaDevCliPro(ni ?: 0)

  fun mudaCliente(): String {
    return ""
  }

  val observacao: String
    get() {
      val parte1 = remarks?.split(")")?.getOrNull(0) ?: return ""
      return "$parte1)"
    }

  companion object {
    fun findCreditoCliente(filtro: FiltroCreditoCliente): List<CreditoCliente> {
      return saci.findCreditoCliente(filtro)
    }
  }
}

data class FiltroCreditoCliente(
  val pesquisa: String,
)