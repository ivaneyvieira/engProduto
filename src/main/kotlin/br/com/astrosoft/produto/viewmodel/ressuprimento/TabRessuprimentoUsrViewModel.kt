package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabRessuprimentoUsrViewModel(val viewModel: RessuprimentoViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabRessuprimentoUsr

  override fun UserSaci.desative() {
    this.ressuprimento = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.ressuprimento
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.ressuprimentoCD = usuario.ressuprimentoCD
    this.ressuprimentoSep = usuario.ressuprimentoSep
    this.ressuprimentoExclui = usuario.ressuprimentoExclui
    this.ressuprimentoEnt = usuario.ressuprimentoEnt
    this.impressoraRessu = usuario.impressoraRessu
    this.ressuprimentoPen = usuario.ressuprimentoPen
    this.ressuprimentoRec = usuario.ressuprimentoRec
    this.listaRessuprimento = usuario.listaRessuprimento
    this.lojaRessu = usuario.lojaRessu
  }
}

interface ITabRessuprimentoUsr : ITabUser
