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
  var autorizado: String?,
  var referente: String?,
  var entregue: String?,
  var recebido: String?,
  var numSing: String?,
  var nameSing: String?,
  var valorTransf: Double?,
  var observacaoTransf: String?
) {
  val sing: String
    get() {
      val numSingVal = numSing ?: return ""
      val nameSingVal = nameSing ?: return ""
      return "$numSingVal - $nameSingVal"
    }

  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  val observacaoLimpa: String
    get() {
      autorizado ?: referente ?: entregue ?: recebido ?: return ""
      return "${autorizado ?: ""} | ${referente ?: ""} | ${entregue ?: ""} | ${recebido ?: ""}"
    }

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
  fun autoriza(user: UserSaci) {
    saci.autorizaPedidoTransf(this, user.no)
  }

  companion object {
    fun findTransf(filtro: FiltroPedidoTransf) = saci.findPedidoTransf(filtro)
  }
}

data class FiltroPedidoTransf(
  val storeno: Int,
  val pesquisa: String,
  val marca: EMarcaPedido,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val autorizado: Boolean?,
)
