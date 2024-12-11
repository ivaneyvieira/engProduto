package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabAcertoUsrViewModel(val viewModel: AcertoEstoqueViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabAcertoUsr

  override fun UserSaci.desative() {
    this.acertoEstoque = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.acertoEstoque
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.acertoEntrada = usuario.acertoEntrada
    this.acertoSaida = usuario.acertoSaida
    this.acertoMovManualEntrada = usuario.acertoMovManualEntrada
    this.acertoMovManualSaida = usuario.acertoMovManualSaida
    this.acertoMovAtacado = usuario.acertoMovAtacado
    this.lojaAcerto = usuario.lojaAcerto
    this.acertoRemoveProd = usuario.acertoRemoveProd
  }
}

interface ITabAcertoUsr : ITabUser
