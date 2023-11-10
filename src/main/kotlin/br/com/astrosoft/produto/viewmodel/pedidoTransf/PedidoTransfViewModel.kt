package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class PedidoTransfViewModel(view: IPedidoTransfView) : ViewModel<IPedidoTransfView>(view) {
  val tabPedidoTransfReservaViewModel = TabPedidoTransfReservaViewModel(this)
  val tabPedidoTransfAutorizadaViewModel = TabPedidoTransfAutorizadaViewModel(this)
  val tabPedidoTransfEntViewModel = TabPedidoTransfEntViewModel(this)

  override fun listTab() = listOf(view.tabPedidoTransfReserva, view.tabPedidoTransfAutorizada, view.tabPedidoTransfEnt)
}

interface IPedidoTransfView : IView {
  val tabPedidoTransfAutorizada: ITabPedidoTransfAutorizada
  val tabPedidoTransfReserva: ITabPedidoTransfReserva
  val tabPedidoTransfEnt: ITabPedidoTransfEnt
}

