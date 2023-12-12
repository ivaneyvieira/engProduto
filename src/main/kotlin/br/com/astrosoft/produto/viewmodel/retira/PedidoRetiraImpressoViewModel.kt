package br.com.astrosoft.produto.viewmodel.retira

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.exec
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.ETipoPedido
import br.com.astrosoft.produto.model.beans.FiltroPedido
import br.com.astrosoft.produto.model.beans.Pedido

class PedidoRetiraImpressoViewModel(val viewModel: PedidoRetiraViewModel) {
  private val subView
    get() = viewModel.view.tabRetiraImpresso

  private fun listPedidosEntregaImpressoSemNota(): List<Pedido> {
    val numPedido = subView.pedidoImpressoa
    return Pedido
      .listaPedidoImpressoSemNota(
        FiltroPedido(
          tipo = ETipoPedido.RETIRA,
          ecommerce = false,
          dataInicial = null,
          dataFinal = null
        )
      )
      .filter { pedido ->
        pedido.pedido == numPedido || numPedido == 0
      }
  }

  fun updateGridImpresso() {
    subView.updateGrid(listPedidosEntregaImpressoSemNota())
  }

  fun desmarcaSemNota() = exec(viewModel.view) {
    val pedidos = subView.itensSelecionados().ifEmpty { fail("Não há pedido selecionado") }
    pedidos.forEach { pedido ->
      pedido.desmarcaImpresso()
    }

    updateGridImpresso()
  }
}

interface IPedidoRetiraImpresso : ITabView {
  fun updateGrid(itens: List<Pedido>)
  fun itensSelecionados(): List<Pedido>
  val pedidoImpressoa: Int
}