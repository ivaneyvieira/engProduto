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
  val total: Double,
  val totalPendente: Double,
  val totalProduto: Double,
  val totalProdutoPendente: Double,
  val notas: List<PedidoNota>,
) {
  val statusPedido: String
    get() = EPedidosStatus.entries.firstOrNull { it.cod == status }?.descricao ?: ""

  companion object {
    fun findPedidoCapa(filtro: FiltroPedidoNota): List<PedidoCapa> {
      val produtos = saci.findPedidoProduto(filtro)
      val pedidos = produtos.toPedidoNota().toPedidoCapa()
      return pedidos
    }
  }
}

fun List<PedidoNota>.toPedidoCapa(): List<PedidoCapa> {
  val grupo = this.groupBy { "${it.loja} ${it.pedido}" }
  return grupo.mapNotNull { entry ->
    val (_, list) = entry
    val pedido = list.firstOrNull() ?: return@mapNotNull null
    PedidoCapa(
      loja = pedido.loja,
      pedido = pedido.pedido,
      data = pedido.data,
      status = pedido.status,
      no = pedido.no,
      fornecedor = pedido.fornecedor,
      total = pedido.total,
      totalPendente = pedido.totalPendente,
      totalProduto = list.sumOf { it.totalProduto },
      totalProdutoPendente = list.sumOf { it.totalProdutoPendente },
      notas = list.filter { it.produtos.isNotEmpty() }
    )

  }
}

data class FiltroPedidoNota(
  val loja: Int,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val pesquisa: String,
  val status: EPedidosStatus,
  val preEntrada: EPreEntrada,
)

enum class EPedidosStatus(val cod: Int, val descricao: String) {
  TODOS(999, "Todos"),
  PENDENTE(0, "Pendente"),
  RECEBIDO(1, "Recebido")
}

enum class EPreEntrada(val
                       cod: String, val descricao: String) {
  SIM("S", "Sim"),
  NAO("N", "Não"),
  TODOS("", "Todos"),
}