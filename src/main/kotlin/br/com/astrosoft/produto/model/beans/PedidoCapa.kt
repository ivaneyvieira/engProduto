package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class PedidoCapa(
  val loja: Int,
  val sigla: String,
  val pedido: Int,
  val data: LocalDate?,
  val status: Int,
  val no: Int,
  val fornecedor: String,
  val totalPedido: Double,
  val totalPendente: Double,
  val frete: Double,
  val totalProduto: Double,
  val totalProdutoPendente: Double,
  val observacao: String,
  val notas: List<PedidoNota>,
) {

  fun produtosCompra(): List<PedidoProdutoCompra> {
    return saci.findPedidoProdutoCompra(loja, pedido)
  }

  val totalRecebido: Double
    get() = totalPedido - totalPendente

  val statusPedido: String
    get() = EPedidosStatus.entries.firstOrNull { it.cod == status }?.descricao ?: ""

  val preEntrada: String
    get() = if (notas.any { it.preEntrada == "S" }) "S" else "N"

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
      sigla = pedido.sigla,
      pedido = pedido.pedido,
      data = pedido.data,
      status = pedido.status,
      no = pedido.no,
      fornecedor = pedido.fornecedor,
      totalPedido = pedido.totalPedido,
      frete = pedido.frete,
      observacao = pedido.observacao,
      totalPendente = pedido.totalPendente,
      totalProduto = list.sumOf { it.totalProduto },
      totalProdutoPendente = list.sumOf { it.totalProdutoPendente },
      notas = list.filter { it.produtos.isNotEmpty() }.filter { it.preEntrada == "S" }
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
  val semRecebimento: Boolean,
)

enum class EPedidosStatus(val cod: Int, val descricao: String) {
  TODOS(999, "Todos"),
  PENDENTE(0, "Pendente"),
  RECEBIDO(1, "Recebido")
}

enum class EPreEntrada(
  val cod: String, val descricao: String
) {
  SIM("S", "Sim"),
  NAO("N", "Não"),
  TODOS("T", "Todos"),
}