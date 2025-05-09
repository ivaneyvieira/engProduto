package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabNotaUsrViewModel(val viewModel: DevFor2ViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabNotaUsr

  override fun UserSaci.desative() {
    this.devFor2 = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.devFor2
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.devFor2NotaPendencia = usuario.devFor2NotaPendencia
    this.devFor2NotaNFD = usuario.devFor2NotaNFD
    this.devFor2NotaTransportadora = usuario.devFor2NotaTransportadora
    this.devFor2NotaEmail = usuario.devFor2NotaEmail
    this.devFor2NotaReposto = usuario.devFor2NotaReposto
    this.devFor2NotaAcerto = usuario.devFor2NotaAcerto
    this.devFor2NotaGarantia = usuario.devFor2NotaGarantia
    this.recebimentoNotaEntrada = usuario.recebimentoNotaEntrada
  }
}

interface ITabNotaUsr : ITabUser
