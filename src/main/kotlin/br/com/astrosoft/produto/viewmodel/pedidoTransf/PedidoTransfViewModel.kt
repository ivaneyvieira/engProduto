package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class PedidoTransfViewModel(view: IPedidoTransfView) : ViewModel<IPedidoTransfView>(view) {
  val tabPedidoTransfReservaViewModel = TabPedidoTransfReservaViewModel(this)
  val tabPedidoTransfImprimirViewModel = TabPedidoTransfImprimirViewModel(this)
  val tabPedidoTransfAutorizadaViewModel = TabPedidoTransfAutorizadaViewModel(this)
  val tabPedidoTransfCD5AViewModel = TabPedidoTransfCD5AViewModel(this)
  val tabPedidoTransfEntViewModel = TabPedidoTransfEntViewModel(this)
  val tabPedidoTransfRessu4ViewModel = TabPedidoTransfRessu4ViewModel(this)
  val tabPedidoTransfUsrViewModel = TabPedidoTransfUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabPedidoTransfReserva,
    view.tabPedidoTransfAutorizada,
    view.tabPedidoTransfCD5A,
    view.tabPedidoTransfEnt,
    view.tabPedidoTransfRessu4,
    view.tabPedidoTransfUsr,
  )
}

interface IPedidoTransfView : IView {
  val tabPedidoTransfImprimir: ITabPedidoTransfImprimir
  val tabPedidoTransfAutorizada: ITabPedidoTransfAutorizada
  val tabPedidoTransfCD5A: ITabPedidoTransfCD5A
  val tabPedidoTransfReserva: ITabPedidoTransfReserva
  val tabPedidoTransfEnt: ITabPedidoTransfEnt
  val tabPedidoTransfRessu4: ITabPedidoTransfRessu4
  val tabPedidoTransfUsr: ITabPedidoTransfUsr
}

