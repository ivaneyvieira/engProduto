package br.com.astrosoft.produto.viewmodel.devFor

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabAvariaRecUsrViewModel(val viewModel: DevolucaoAvariaRecViewModel) : TabUsrViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabAvariaRecUsr

  override fun UserSaci.desative() {
    this.menuDevolucaoAvariaRec = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.menuDevolucaoAvariaRec
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.avariaRecEditor = usuario.avariaRecEditor
    this.avariaRecPendente = usuario.avariaRecPendente
    this.avariaRecTransportadora = usuario.avariaRecTransportadora
    this.avariaRecEmail = usuario.avariaRecEmail
    this.avariaRecAcerto = usuario.avariaRecAcerto
    this.avariaRecNFD = usuario.avariaRecNFD
    this.avariaRecReposto = usuario.avariaRecReposto
  }
}

interface ITabAvariaRecUsr : ITabUser
