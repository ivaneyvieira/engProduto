package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.PedidoAcerto

class TabAcertoPedidoViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoPedido

  fun updateView() = viewModel.exec {
    val pedido = PedidoAcerto.findPedidoAcerto()
    subView.updatePedido(pedido)
  }
}

interface ITabAcertoPedido : ITabView {
  fun updatePedido(pedidos: List<PedidoAcerto>)
}
