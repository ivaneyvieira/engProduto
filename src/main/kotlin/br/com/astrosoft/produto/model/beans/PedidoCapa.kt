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
  val invno: Int?,
  val dataEmissao: LocalDate?,
  val dataEntrada: LocalDate?,
  val nfEntrada: String?,
  val totalProduto: Double,
  val totalProdutoPendente: Double,
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
  return this.groupBy { "${it.loja} ${it.pedido} ${it.invno ?: 0}" }
    .map { (key, list) ->
      val pedidoCapa = list.firstOrNull()
      val produtos = list.distinctBy { "${it.loja} ${it.pedido} ${it.prdno} ${it.grade}" }
      PedidoCapa(
        loja = pedidoCapa?.loja ?: 0,
        pedido = pedidoCapa?.pedido ?: 0,
        invno = pedidoCapa?.invno,
        dataEmissao = pedidoCapa?.dataEmissao,
        dataEntrada = pedidoCapa?.dataEntrada,
        nfEntrada = pedidoCapa?.nfEntrada,
        data = pedidoCapa?.data,
        status = pedidoCapa?.status ?: 0,
        no = pedidoCapa?.no ?: 0,
        fornecedor = pedidoCapa?.fornecedor ?: "",
        totalProduto = produtos.sumOf { it.totalProduto },
        totalProdutoPendente = produtos.sumOf { it.totalProdutoPendente },
        produtos = produtos,
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