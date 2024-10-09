package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class PedidoCapa(
  val loja: Int,
  val pedido: Int,
  val data: LocalDate?,
  val status: Int,
  val no: Int,
  val fornecedor: String,
  var totalProduto: Double,
  var totalProdutoPendente: Double,
  val produtos: List<PedidoProduto>,
) {
  val statusPedido: String
    get() = EPedidosStatus.entries.firstOrNull { it.cod == status }?.descricao ?: ""

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
        status = pedidoCapa?.status ?: 0,
        no = pedidoCapa?.no ?: 0,
        fornecedor = pedidoCapa?.fornecedor ?: "",
        totalProduto = list.sumOf { it.totalProduto },
        totalProdutoPendente = list.sumOf { it.totalProdutoPendente },
        produtos = list
      )
    }
}

data class FiltroPedidoCapa(
  val loja: Int,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val pesquisa: String,
  val status: EPedidosStatus,
)

enum class EPedidosStatus(val cod: Int, val descricao: String) {
  TODOS(999, "Todos"),
  ABERTOS(0, "Pendente"),
  FECHADOS(1, "Recebido")
}