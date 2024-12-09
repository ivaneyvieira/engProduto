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

  val rotaRessuprimento: String?
    get() {
      val loja = pedido?.toString()?.substring(0, 1) ?: return null
      return "${loja}4"
    }

  fun duplicaPedido(): PedidoNovo? {
    return saci.duplicaPedido(this)
  }

  fun removerPedido() {
    val ordno = this.pedido ?: return
    saci.removerPedido(ordno)
  }

  fun produtos(): List<ProdutoRessuprimento> {
    return saci.findProdutoRessuprimento(pedido = this).map{prd ->
      if(prd.localizacao.isNullOrBlank()) {
        prd.localizacao = LocalizacaoAlternativa.locsForn(prd.vendno ?: 0)
      }
      prd
    }
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
