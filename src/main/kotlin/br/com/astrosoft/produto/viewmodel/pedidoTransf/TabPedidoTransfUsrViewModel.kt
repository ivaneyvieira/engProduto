package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabPedidoTransfUsrViewModel(val viewModel: PedidoTransfViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabPedidoTransfUsr

  override fun UserSaci.desative() {
    this.pedidoTransf = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.pedidoTransf
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.pedidoTransfReserva = usuario.pedidoTransfReserva
    this.pedidoTransfAutorizada = usuario.pedidoTransfAutorizada
    this.pedidoTransfEnt = usuario.pedidoTransfEnt
    this.pedidoTransfRessu4 = usuario.pedidoTransfRessu4
    this.impressoraTrans = usuario.impressoraTrans
  }
}

interface ITabPedidoTransfUsr : ITabUser
