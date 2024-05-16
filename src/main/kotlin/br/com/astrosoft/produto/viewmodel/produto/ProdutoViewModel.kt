package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ProdutoViewModel(view: IProdutoView) : ViewModel<IProdutoView>(view) {
  val tabProdutoListViewModel = TabProdutoListViewModel(this)
  val tabProdutoUsrViewModel = TabProdutoUsrViewModel(this)
  val tabEstoqueTotalViewModel = TabEstoqueTotalViewModel(this)

  override fun listTab() = listOf(
    view.tabProdutoList,
    view.tabEstoqueTotalViewModel,
    view.tabProdutoUsr,
  )
}

interface IProdutoView : IView {
  val tabProdutoList: ITabProdutoList
  val tabProdutoUsr: ITabProdutoUsr
  val tabEstoqueTotalViewModel: ITabEstoqueTotalViewModel
}

