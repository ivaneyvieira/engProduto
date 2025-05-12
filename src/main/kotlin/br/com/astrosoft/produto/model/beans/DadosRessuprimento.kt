package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class DadosRessuprimento (
  val loja: Int,
  val lojaRessuprimento: Int,
  val pedido: Int,
  val data: LocalDate?,
  val codFornecedor: Int,
  val totalPedido: Double,
  val observacao: String,
  val produtos: List<DadosProdutosRessuprimento>
){
  companion object {
    fun find(filter: FiltroDadosProdutosRessuprimento): List<DadosRessuprimento> {
      return saci.findDadosRessuprimento(filter).toDadosRessuprimento()
    }
  }
}

fun List<DadosProdutosRessuprimento>.toDadosRessuprimento(): List<DadosRessuprimento> {
  return this.groupBy { "${it.pedido} ${it.lojaRessuprimento}" }.map { (pedido, produtos) ->
    val first = produtos.firstOrNull()
    DadosRessuprimento(
      loja = first?.loja ?: 0,
      lojaRessuprimento = first?.lojaRessuprimento ?: 0,
      pedido = first?.pedido ?: 0,
      data = first?.data,
      codFornecedor = first?.codFornecedor ?: 0,
      totalPedido = first?.totalPedido ?: 0.00,
      observacao = first?.observacao ?: "",
      produtos = produtos
    )
  }
}