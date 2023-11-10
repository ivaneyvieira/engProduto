package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class PedidoTransfViewModel(view: IPedidoTransfView) : ViewModel<IPedidoTransfView>(view) {
  val tabPedidoTransfReservaViewModel = TabPedidoTransfReservaViewModel(this)
  val tabPedidoTransfEntViewModel = TabPedidoTransfEntViewModel(this)

  override fun listTab() = listOf(view.tabPedidoTransfReserva, view.tabPedidoTransfEnt)
}

interface IPedidoTransfView : IView {
  val tabPedidoTransfReserva: ITabPedidoTransfReserva
  val tabPedidoTransfEnt: ITabPedidoTransfEnt
}

