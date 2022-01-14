package br.com.astrosoft.produto.viewmodel.pedido

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class PedidoViewModel(view: IPedidoView) : ViewModel<IPedidoView>(view) {
  val tabPedidoCDViewModel = TabPedidoCDViewModel(this)
  val tabPedidoEntViewModel = TabPedidoEntViewModel(this)

  override fun listTab() = listOf(view.tabPedidoCD, view.tabPedidoEnt)
}

interface IPedidoView : IView {
  val tabPedidoCD: ITabPedidoCD
  val tabPedidoEnt: ITabPedidoEnt
}

