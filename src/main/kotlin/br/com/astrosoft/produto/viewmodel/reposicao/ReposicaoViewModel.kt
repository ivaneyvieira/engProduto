package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ReposicaoViewModel(view: IReposicaoView) : ViewModel<IReposicaoView>(view) {
  val tabReposicaoSepViewModel = TabReposicaoSepViewModel(this)
  val tabReposicaoEntViewModel = TabReposicaoEntViewModel(this)
  override fun listTab() = listOf(
    view.tabReposicaoSep,
    view.tabReposicaoEnt,
  )
}

interface IReposicaoView : IView {
  val tabReposicaoSep: ITabReposicaoSep
  val tabReposicaoEnt: ITabReposicaoEnt
}