package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

data class PedidoNota(
  val loja: Int,
  val sigla: String,
  val pedido: Int,
  val data: LocalDate?,
  val status: Int,
  val no: Int,
  val fornecedor: String,
  val preEntrada: String,
  val totalPedido: Double,
  val frete: Double,
  val observacao: String,
  val totalPendente: Double,
  val invno: Int?,
  val tipo: String,
  val dataEmissao: LocalDate?,
  val dataEntrada: LocalDate?,
  val nfEntrada: String?,
  val totalProduto: Double,
  val totalProdutoPendente: Double,
  val produtos: List<PedidoProduto>,
) {
  val tipoNota: String
    get() = when (tipo) {
      "P" -> "Pre-Entrada"
      "N" -> "Entrada"
      else -> ""
    }
}

fun List<PedidoProduto>.toPedidoNota(): List<PedidoNota> {
  return this.groupBy { "${it.loja} ${it.pedido} ${it.invno ?: 0}" }
    .mapNotNull { (_, produtos) ->
      val pedidoCapa = produtos.firstOrNull() ?: return@mapNotNull null
      PedidoNota(
        loja = pedidoCapa.loja,
        sigla = pedidoCapa.sigla,
        pedido = pedidoCapa.pedido,
        invno = pedidoCapa.invno,
        tipo = pedidoCapa.tipo,
        dataEmissao = pedidoCapa.dataEmissao,
        dataEntrada = pedidoCapa.dataEntrada,
        nfEntrada = pedidoCapa.nfEntrada,
        data = pedidoCapa.data,
        status = pedidoCapa.status,
        no = pedidoCapa.no,
        fornecedor = pedidoCapa.fornecedor,
        preEntrada = pedidoCapa.preEntrada,
        totalPedido = pedidoCapa.totalPedido,
        frete = pedidoCapa.frete,
        observacao = pedidoCapa.observacao,
        totalPendente = pedidoCapa.totalPendente,
        totalProduto = produtos.sumOf { it.totalProduto },
        totalProdutoPendente = produtos.sumOf { it.totalProdutoPendente },
        produtos = produtos.filter { it.prdno != "" },
      )
    }
}
