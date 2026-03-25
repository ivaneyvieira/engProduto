package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class VendaRefUsrViewModel(val viewModel: VendaRefViewModel) : TabUsrViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabVendaRefUsr

  override fun UserSaci.desative() {
    this.vendaRef = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.vendaRef
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.tabVendaRef = usuario.tabVendaRef
    this.tabResumo = usuario.tabResumo
    this.tabResumoPgto = usuario.tabResumoPgto
  }
}

interface ITabVendaRefUsr : ITabUser

