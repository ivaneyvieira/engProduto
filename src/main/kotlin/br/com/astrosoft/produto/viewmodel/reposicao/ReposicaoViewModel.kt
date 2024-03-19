package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ReposicaoViewModel(view: IReposicaoView) : ViewModel<IReposicaoView>(view) {
  val tabReposicaoSepViewModel = TabReposicaoSepViewModel(this)
  val tabReposicaoEntViewModel = TabReposicaoEntViewModel(this)
  val tabReposicaoUsrViewModel = TabReposicaoUsrViewModel(this)
  override fun listTab() = listOf(
    view.tabReposicaoSep,
    view.tabReposicaoEnt,
    view.tabReposicaoUsr,
  )
}

interface IReposicaoView : IView {
  val tabReposicaoSep: ITabReposicaoSep
  val tabReposicaoEnt: ITabReposicaoEnt
  val tabReposicaoUsr: ITabReposicaoUsr
}