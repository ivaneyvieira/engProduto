package br.com.astrosoft.produto.viewmodel.expedicao

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
    this.notaSep = usuario.notaSep
    this.notaRota = usuario.notaRota
    this.notaTroca = usuario.notaTroca
    this.notaExp = usuario.notaExp
    this.notaCD = usuario.notaCD
    this.notaEnt = usuario.notaEnt
    this.impressoraNota = usuario.impressoraNota
    this.lojaNota = usuario.lojaNota
    this.localizacaoNota = usuario.localizacaoNota
    this.lojaLocExpedicao = usuario.lojaLocExpedicao
    this.impressoraNotaTermica = usuario.impressoraNotaTermica
  }
}

interface ITabNotaUsr : ITabUser
