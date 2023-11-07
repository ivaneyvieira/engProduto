package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class PedidoTransf(
  var loja: Int,
  var lojaOrigem: String?,
  var ordno: String?,
  var lojaDestino: String?,
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

  fun produtos(marca: EMarcaPedido) = saci.findProdutoPedidoTransf(this, marca, userLocais())

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
