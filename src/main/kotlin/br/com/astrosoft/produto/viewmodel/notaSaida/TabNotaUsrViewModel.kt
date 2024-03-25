package br.com.astrosoft.produto.viewmodel.notaSaida

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabNotaUsrViewModel(val viewModel: NotaViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabNotaUsr

  override fun UserSaci.desative() {
    this.nota = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.nota
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.notaExp = usuario.notaExp
    this.notaCD = usuario.notaCD
    this.notaEnt = usuario.notaEnt
    this.tipoNota = usuario.tipoNota
    this.impressoraNota = usuario.impressoraNota
    this.lojaNota = usuario.lojaNota
  }
}

interface ITabNotaUsr : ITabUser
