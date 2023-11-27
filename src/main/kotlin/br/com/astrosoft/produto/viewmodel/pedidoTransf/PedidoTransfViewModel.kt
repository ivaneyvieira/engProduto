package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class PedidoTransfViewModel(view: IPedidoTransfView) : ViewModel<IPedidoTransfView>(view) {
  val tabPedidoTransfReservaViewModel = TabPedidoTransfReservaViewModel(this)
  val tabPedidoTransfAutorizadaViewModel = TabPedidoTransfAutorizadaViewModel(this)
  val tabPedidoTransfImpressoViewModel = TabPedidoTransfImpressoViewModel(this)
  val tabPedidoTransfEntViewModel = TabPedidoTransfEntViewModel(this)
  val tabPedidoTransfRessu4ViewModel = TabPedidoTransfRessu4ViewModel(this)

  override fun listTab() = listOf(
    view.tabPedidoTransfReserva,
    view.tabPedidoTransfAutorizada,
    //view.tabPedidoTransfImpresso,
    view.tabPedidoTransfEnt,
    view.tabPedidoTransfRessu4
  )
}

interface IPedidoTransfView : IView {
  val tabPedidoTransfAutorizada: ITabPedidoTransfAutorizada
  val tabPedidoTransfImpresso: ITabPedidoTransfImpresso
  val tabPedidoTransfReserva: ITabPedidoTransfReserva
  val tabPedidoTransfEnt: ITabPedidoTransfEnt
  val tabPedidoTransfRessu4: ITabPedidoTransfRessu4
}

