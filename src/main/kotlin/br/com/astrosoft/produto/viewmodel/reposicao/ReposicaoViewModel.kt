package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ReposicaoViewModel(view: IReposicaoView) : ViewModel<IReposicaoView>(view) {
  val tabReposicaoCDViewModel = TabReposicaoCDViewModel(this)
  val tabReposicaoSepViewModel = TabReposicaoSepViewModel(this)
  override fun listTab() = listOf(
    view.tabReposicaoCD,
    view.tabReposicaoSep,
  )
}

interface IReposicaoView : IView {
  val tabReposicaoCD: ITabReposicaoCD
  val tabReposicaoSep: ITabReposicaoSep
}