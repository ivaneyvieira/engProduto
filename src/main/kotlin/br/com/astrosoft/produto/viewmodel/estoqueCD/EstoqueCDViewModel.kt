package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class EstoqueCDViewModel(view: IEstoqueCDView) : ViewModel<IEstoqueCDView>(view) {
  val tabEstoqueMovViewModel = TabEstoqueMovViewModel(this)
  val tabEstoqueAcertoViewModel = TabEstoqueAcertoViewModel(this)
  val tabEstoqueAcertoMobileViewModel = TabEstoqueAcertoMobileViewModel(this)
  val tabEstoqueSaldoViewModel = TabEstoqueSaldoViewModel(this)
  val tabEstoqueConfViewModel = TabEstoqueConfViewModel(this)
  val tabEstoqueInventarioViewModel = TabEstoqueInventarioViewModel(this)
  val tabEstoqueCadViewModel = TabEstoqueCadViewModel(this)
  val tabEstoqueCD1AViewModel = TabEstoqueCD1AViewModel(this)
  val tabEstoqueUsrViewModel = TabEstoqueUsrViewModel(this)
  val tabValidadeListViewModel = TabValidadeListViewModel(this)

  override fun listTab() = listOf(
    view.tabEstoqueSaldo,
    view.tabEstoqueConf,
    view.tabEstoqueAcerto,
    view.tabEstoqueAcertoMobile,
    view.tabEstoqueMov,
    view.tabEstoqueCad,
    view.tabEstoqueCD1A,
    view.tabValidadeList,
    view.tabEstoqueUsr,
  )
}

interface IEstoqueCDView : IView {
  val tabEstoqueMov: ITabEstoqueMov
  val tabEstoqueAcerto: ITabEstoqueAcerto
  val tabEstoqueAcertoMobile: ITabEstoqueAcertoMobile
  val tabEstoqueSaldo: ITabEstoqueSaldo
  val tabEstoqueConf: ITabEstoqueConf
  val tabEstoqueInventario: ITabEstoqueInventario
  val tabEstoqueCad: ITabEstoqueCad
  val tabEstoqueCD1A: ITabEstoqueCD1A
  val tabEstoqueUsr: ITabEstoqueUsr
  val tabValidadeList: ITabValidadeList
}