package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class PedidoVenda(
  val loja: Int,
  val ordno: Int,
  val nota: String,
  val cliente: Int,
  val data: LocalDate,
  val vendedor: Int,
  val localizacao: String?,
  val usuarioCD: String?,
  val totalProdutos: Double,
  val marca: Int?,
  val cancelada: String?
) {
  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  private fun splitCD(index: Int) = usuarioCD?.split("-")?.getOrNull(index) ?: ""

  val usuarioNameCD
    get() = splitCD(0)
  val dataCD
    get() = splitCD(1)
  val horaCD
    get() = splitCD(2)

  val chaveNovaCD: String
    get() = usuarioNameCD + "-" + dataCD + "-" + horaCD + "-" + localizacao

  fun produtos(marca: EMarcaPedido) = saci.findProdutoPedidoVenda(this, marca, userLocais())

  companion object {
    fun find(filtro: FiltroPedido) = saci.findPedidoVenda(filtro, userLocais())
  }
}

data class FiltroPedido(val storeno: Int, val ordno: Int, val marca: EMarcaPedido)

enum class EMarcaPedido(val num: Int, val descricao: String) {
  CD(0, "CD"), ENT(1, "Entregue"), TODOS(999, "Todos")
}