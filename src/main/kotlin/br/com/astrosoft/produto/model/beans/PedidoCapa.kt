package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class PedidoCapa(
  val loja: Int,
  val pedido: Int,
  val data: LocalDate?,
  val no: Int,
  val fornecedor: String,
  val total: Double,
  val produtos: List<PedidoProduto>,
) {
  companion object {
    fun findPedidoCapa(filtro: FiltroPedidoCapa): List<PedidoCapa> {
      val produtos = saci.findPedidoProduto(filtro)
      val pedidos = produtos.toPedidoCapa()
      return pedidos
    }
  }
}

fun List<PedidoProduto>.toPedidoCapa(): List<PedidoCapa> {
  return this.groupBy { it.loja to it.pedido }
    .map { (key, list) ->
      val (loja, pedido) = key
      val pedidoCapa = list.firstOrNull()
      PedidoCapa(
        loja = loja,
        pedido = pedido,
        data = pedidoCapa?.data,
        no = pedidoCapa?.no ?: 0,
        fornecedor = pedidoCapa?.fornecedor ?: "",
        total = pedidoCapa?.total ?: 0.0,
        produtos = list
      )
    }
}

data class FiltroPedidoCapa(
  val loja: Int,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val pesquisa: String,
)