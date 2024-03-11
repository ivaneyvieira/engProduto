package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class EstoqueCDViewModel(view: IEstoqueCDView) : ViewModel<IEstoqueCDView>(view) {
  val tabEstoqueMFViewModel = TabEstoqueMFViewModel(this)
  val tabEstoqueCadViewModel = TabEstoqueCadViewModel(this)
  val tabEstoqueCD1AViewModel = TabEstoqueCD1AViewModel(this)

  override fun listTab() = listOf(
    view.tabEstoqueMF,
    view.tabEstoqueCad,
    view.tabEstoqueCD1A,
  )
}

interface IEstoqueCDView : IView {
  val tabEstoqueMF: ITabEstoqueMF
  val tabEstoqueCad: ITabEstoqueCad
  val tabEstoqueCD1A: ITabEstoqueCD1A
}