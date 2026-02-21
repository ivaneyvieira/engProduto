package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ReposicaoViewModel(view: IReposicaoView) : ViewModel<IReposicaoView>(view) {
  val tabReposicaoSepViewModel = TabReposicaoSepViewModel(this)
  val tabReposicaoMovViewModel = TabReposicaoRepViewModel(this)
  val tabReposicaoAcertoViewModel = TabReposicaoAcertoViewModel(this)
  val tabReposicaoRetornoViewModel = TabReposicaoRetornoViewModel(this)
  val tabReposicaoEntViewModel = TabReposicaoEntViewModel(this)
  val tabReposicaoUsrViewModel = TabReposicaoUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabReposicaoSep,
    view.tabReposicaoMov,
    view.tabReposicaoAcerto,
    view.tabReposicaoRetorno,
    view.tabReposicaoEnt,
    view.tabReposicaoUsr,
  )
}

interface IReposicaoView : IView {
  val tabReposicaoSep: ITabReposicaoSep
  val tabReposicaoMov: ITabReposicaoRep
  val tabReposicaoAcerto: ITabReposicaoAcerto
  val tabReposicaoRetorno: ITabReposicaoRetorno
  val tabReposicaoEnt: ITabReposicaoEnt
  val tabReposicaoUsr: ITabReposicaoUsr
}