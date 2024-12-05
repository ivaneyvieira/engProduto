package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class PedidoRessuprimento {
  var loja: Int? = null
  var sigla: String? = null
  var pedido: Int? = null
  var data: LocalDate? = null
  var status: String? = null
  var vendno: Int? = null
  var fornecedor: String? = null
  var totalPedido: Double? = null
  var frete: Double? = null
  var totalPendente: Double? = null
  var observacao: String? = null


  fun produtos(): List<ProdutoRessuprimento> {
    return saci.findProdutoRessuprimento(pedido = this)
  }

  companion object {
    fun findPedidoRessuprimento(filtro: FiltroPedidoRessuprimento): List<PedidoRessuprimento> {
      return saci.findPedidosRessuprimento(filtro)
    }
  }
}

data class FiltroPedidoRessuprimento(
  val pesquisa: String
)
