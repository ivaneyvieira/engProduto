package br.com.astrosoft.produto.viewmodel.nfd

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabNfdUsrViewModel(val viewModel: NfdViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabNfdUsr

  override fun UserSaci.desative() {
    this.nfd = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.nfd
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.nfdDevFor = usuario.nfdDevFor
  }
}

interface ITabNfdUsr : ITabUser
