package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.devolucao.model.beans.FiltroAgenda
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroPedidoCapa
import br.com.astrosoft.produto.model.beans.PedidoCapa

class TabPedidoViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabPedido

  fun updateView() {
    val filtro = subView.filtro()
    val pedidos = PedidoCapa.findPedidoCapa(filtro)
    subView.updatePedidos(pedidos)
  }
}

interface ITabPedido : ITabView {
  fun filtro(): FiltroPedidoCapa
  fun updatePedidos(pedido: List<PedidoCapa>)
}