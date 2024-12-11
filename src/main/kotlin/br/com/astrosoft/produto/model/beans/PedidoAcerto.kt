package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class PedidoAcerto {
  var loja: Int? = null
  var lojaPedido: Int? = null
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

  val rotaAcerto: String?
    get() {
      val loja = pedido?.toString()?.substring(0, 1) ?: return null
      return "${loja}4"
    }

  fun produtos(): List<ProdutoAcerto> {
    return saci.findProdutoAcerto(pedido = this).map { prd ->
      if (prd.localizacao.isNullOrBlank()) {
        prd.localizacao = LocalizacaoAlternativa.locsForn(prd.vendno ?: 0)
      }
      prd
    }.filter {
      (it.qtPedido ?: 0) > 0
    }
  }

  companion object {
    fun findPedidoAcerto(): List<PedidoAcerto> {
      return saci.findPedidosAcerto()
    }
  }
}
