package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabReposicaoUsrViewModel(val viewModel: ReposicaoViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabReposicaoUsr

  override fun UserSaci.desative() {
    this.reposicao = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.reposicao
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.reposicaoEnt = usuario.reposicaoEnt
    this.reposicaoSep = usuario.reposicaoSep
    this.impressoraRepo = usuario.impressoraRepo
    this.localizacaoRepo = usuario.localizacaoRepo
    this.tipoMetodo = usuario.tipoMetodo
  }
}

interface ITabReposicaoUsr : ITabUser
