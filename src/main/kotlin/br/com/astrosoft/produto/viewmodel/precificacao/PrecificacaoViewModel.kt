package br.com.astrosoft.produto.viewmodel.precificacao

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class PrecificacaoViewModel(view: IPrecificacaoView) : ViewModel<IPrecificacaoView>(view) {
  val tabPrecificacaoViewModel = TabPrecificacaoViewModel(this)
  val tabPrecificacaoEntradaViewModel = TabPrecificacaoEntradaViewModel(this)
  val tabPrecificacaoEntradaMaViewModel = TabPrecificacaoEntradaMaViewModel(this)
  val tabPrecificacaoSaidaViewModel = TabPrecificacaoSaidaViewModel(this)
  val tabPrecificacaoUsrViewModel = TabPrecificacaoUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabPrecificacaoViewModel,
    view.tabPrecificacaoEntradaViewModel,
    view.tabPrecificacaoSaidaViewModel,
    view.tabPrecificacaoEntradaMaViewModel,
    view.tabPrecificacaoUsr
  )
}

interface IPrecificacaoView : IView {
  val tabPrecificacaoViewModel: ITabPrecificacaoViewModel
  val tabPrecificacaoEntradaViewModel: ITabPrecificacaoViewModel
  val tabPrecificacaoEntradaMaViewModel: ITabPrecificacaoViewModel
  val tabPrecificacaoSaidaViewModel: ITabPrecificacaoViewModel
  val tabPrecificacaoUsr: ITabPrecificacaoUsr
}
