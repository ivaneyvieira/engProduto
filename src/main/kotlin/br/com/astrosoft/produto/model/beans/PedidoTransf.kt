package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class PedidoTransf(
  var loja: Int,
  var lojaOrigem: String?,
  var ordno: String?,
  var lojaDestino: String?,
  var rota: String?,
  var cliente: Int?,
  var userno: Int?,
  var usuario: String?,
  var data: LocalDate?,
  var vendedor: Int?,
  var localizacao: String?,
  var usuarioCD: String?,
  var totalProdutos: Double?,
  var marca: Int?,
  var cancelada: String?,
  var hora: LocalTime?,
  var situacaoPedido: String?,
  var observacao: String?,
  var dataTransf: LocalDate?,
  var notaTransf: String?,
) {

  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  val observacaoLimpa
    get() = observacao?.replace(" +".toRegex(), " ")

  private fun splitCD(index: Int) = usuarioCD?.split("-")?.getOrNull(index) ?: ""

  val usuarioNameCD
    get() = splitCD(0)
  val dataCD
    get() = splitCD(1)
  val horaCD
    get() = splitCD(2)

  val chaveNovaCD: String
    get() = "$usuarioNameCD-$dataCD-$horaCD-$localizacao"

  fun produtos() = saci.findProdutoPedidoTransf(this)

  companion object {
    fun findTransf(filtro: FiltroPedidoTransf) = saci.findPedidoTransf(filtro)
  }
}

data class FiltroPedidoTransf(
  val storeno: Int,
  val pesquisa: String,
  val marca: EMarcaPedido,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?
)
