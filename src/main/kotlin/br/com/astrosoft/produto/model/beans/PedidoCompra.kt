package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class PedidoCompra(val loja: Int,
                val ordno: Int,
                val numero: Int,
                val serie: String,
                val cliente: Int,
                val data: LocalDate,
                val vendedor: Int,
                val localizacao: String?,
                val usuarioExp: String?,
                val usuarioCD: String?,
                val totalProdutos: Double,
                val marca: Int?,
                val cancelada: String?) {
  val nota
    get() = "$numero/$serie"

  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  val chaveExp: String?
    get() {
      val split = usuarioExp?.split("_") ?: return null
      val usuario = split.getOrNull(0) ?: ""
      val data = split.getOrNull(1) ?: ""
      val hora = split.getOrNull(2) ?: ""
      return usuario + "_" + nota + "_" + data + "_" + hora + "_" + localizacao
    }

  val chaveCD: String?
    get() {
      val split = usuarioCD?.split("_") ?: return null
      val usuario = split.getOrNull(0) ?: ""
      val data = split.getOrNull(1) ?: ""
      val hora = split.getOrNull(2) ?: ""
      return "Entregue" + "_" + usuario + "_" + nota + "_" + data + "_" + hora + "_" + localizacao
    }

  fun produtos(marca: EMarcaPedido) = saci.findProdutoPedidoCompra(this, marca, userLocais())

  companion object {
    fun find(filtro: FiltroPedido) = saci.findPedidoCompra(filtro, userLocais())
  }
}

data class FiltroPedido(val storeno: Int, val ordno: Int, val marca: EMarcaNota)

enum class EMarcaPedido(val num: Int, val descricao: String) {
  CD(0, "CD"), ENT(1, "Entregue"), TODOS(999, "Todos")
}