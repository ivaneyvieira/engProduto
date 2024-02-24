package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class EstoqueCDViewModel(view: IEstoqueCDView) : ViewModel<IEstoqueCDView>(view) {
  val tabEstoqueMFViewModel = TabEstoqueMFViewModel(this)
  val tabEstoqueCadViewModel = TabEstoqueCadViewModel(this)

  override fun listTab() = listOf(
    view.tabEstoqueMF,
    view.tabEstoqueCad,
  )
}

interface IEstoqueCDView : IView {
  val tabEstoqueMF: ITabEstoqueMF
  val tabEstoqueCad: ITabEstoqueCad
}