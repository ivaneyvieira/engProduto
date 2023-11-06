package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class PedidoTransfViewModel(view: IPedidoTransfView) : ViewModel<IPedidoTransfView>(view) {
  val tabPedidoTransfCDViewModel = TabPedidoTransfCDViewModel(this)
  val tabPedidoTransfEntViewModel = TabPedidoTransfEntViewModel(this)

  override fun listTab() = listOf(view.tabPedidoTransfCD, view.tabPedidoTransfEnt)
}

interface IPedidoTransfView : IView {
  val tabPedidoTransfCD: ITabPedidoTransfCD
  val tabPedidoTransfEnt: ITabPedidoTransfEnt
}

