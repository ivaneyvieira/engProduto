package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class TransfRessu4(
  var loja: Int,
  var pdvno: Int,
  var transacao: Int,
  var lojaOrigem: String?,
  var lojaDestino: String?,
  var rota: String?,
  var ordno: String?,
  var cliente: Int?,
  var data: LocalDate?,
  var vendedor: Int?,
  var userno: Int?,
  var usuario: String?,
  var notaTransf: String?,
  var valorTransf: Double?,
  var observacaoTransf: String?,
) {

  fun produtos() = saci.findProdutoPedidoRessu4(this)

  companion object {
    fun findAll(filtro: FiltroPedidoRessu4): List<TransfRessu4> {
      return saci.findPedidoRessu4(filtro)
    }
  }
}

data class FiltroPedidoRessu4(
  val storeno: Int,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)