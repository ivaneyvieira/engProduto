package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class EstoqueCDViewModel(view: IEstoqueCDView) : ViewModel<IEstoqueCDView>(view) {
  val tabEstoqueMFViewModel = TabEstoqueMovViewModel(this)
  val tabEstoqueSaldoViewModel = TabEstoqueSaldoViewModel(this)
  val tabEstoqueCadViewModel = TabEstoqueCadViewModel(this)
  val tabEstoqueCD1AViewModel = TabEstoqueCD1AViewModel(this)
  val tabEstoqueUsrViewModel = TabEstoqueUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabEstoqueMov,
    view.tabEstoqueSaldo,
    view.tabEstoqueCad,
    view.tabEstoqueCD1A,
    view.tabEstoqueUsr,
  )
}

interface IEstoqueCDView : IView {
  val tabEstoqueMov: ITabEstoqueMov
  val tabEstoqueSaldo: ITabEstoqueSaldo
  val tabEstoqueCad: ITabEstoqueCad
  val tabEstoqueCD1A: ITabEstoqueCD1A
  val tabEstoqueUsr: ITabEstoqueUsr
}