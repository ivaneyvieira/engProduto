package br.com.astrosoft.produto.viewmodel.cliente

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabClienteUsrViewModel(val viewModel: ClienteViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabClienteUsr

  override fun UserSaci.desative() {
    this.cliente = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.cliente
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.clienteCadastro = usuario.clienteCadastro
  }
}

interface ITabClienteUsr : ITabUser
