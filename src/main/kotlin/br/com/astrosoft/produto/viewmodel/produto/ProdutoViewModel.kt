package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ProdutoViewModel(view: IProdutoView) : ViewModel<IProdutoView>(view) {
  val tabProdutoListViewModel = TabProdutoListViewModel(this)
  val tabProdutoInventarioViewModel = TabProdutoInventarioViewModel(this)
  val tabProdutoUsrViewModel = TabProdutoUsrViewModel(this)
  val tabEstoqueGiroViewModel = TabEstoqueGiroViewModel(this)
  val tabEstoqueValidadeViewModel = TabEstoqueValidadeViewModel(this)

  override fun listTab() = listOf(
    view.tabProdutoList,
    view.tabEstoqueGiroViewModel,
    view.tabEstoqueValidadeViewModel,
    view.tabProdutoInventario,
    view.tabProdutoUsr,
  )
}

interface IProdutoView : IView {
  val tabProdutoList: ITabProdutoList
  val tabProdutoInventario: ITabProdutoInventario
  val tabProdutoUsr: ITabProdutoUsr
  val tabEstoqueGiroViewModel: ITabEstoqueGiroViewModel
  val tabEstoqueValidadeViewModel: ITabEstoqueValidadeViewModel
}

