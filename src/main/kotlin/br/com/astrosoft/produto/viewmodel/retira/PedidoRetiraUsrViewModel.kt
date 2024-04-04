package br.com.astrosoft.produto.viewmodel.retira

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class PedidoRetiraUsrViewModel(val viewModel: PedidoRetiraViewModel) : TabUsrViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabRetiraUsr

  override fun UserSaci.desative() {
    this.pedidoRetira = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.pedidoTransf
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.retiraImprimir = usuario.retiraImprimir
    this.retiraImpresso = usuario.retiraImpresso
    this.impressoraRet = usuario.impressoraRet
  }
}

interface ITabPedidoRetiraUsr : ITabUser
