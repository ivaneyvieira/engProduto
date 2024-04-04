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
    this.notaExp = usuario.notaExp
    this.notaCD = usuario.notaCD
    this.notaEnt = usuario.notaEnt
    this.tipoNota = usuario.tipoNota
    this.impressoraNota = usuario.impressoraNota
    this.lojaNota = usuario.lojaNota
    this.localizacaoNota = usuario.localizacaoNota
    this.entregaNota = usuario.entregaNota
  }
}

interface ITabPedidoTransfUsr : ITabUser
