package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.promocao.viewmodel.produto.ITabEstoqueGeralViewModel
import br.com.astrosoft.promocao.viewmodel.produto.TabEstoqueGeralViewModel

class ProdutoViewModel(view: IProdutoView) : ViewModel<IProdutoView>(view) {
  val tabProdutoListViewModel = TabProdutoListViewModel(this)
  val tabProdutoUsrViewModel = TabProdutoUsrViewModel(this)
  val tabEstoqueGeralViewModel = TabEstoqueGeralViewModel(this)

  override fun listTab() = listOf(
    view.tabProdutoList,
    view.tabEstoqueGeralViewModel,
    view.tabProdutoUsr,
  )
}

interface IProdutoView : IView {
  val tabProdutoList: ITabProdutoList
  val tabProdutoUsr: ITabProdutoUsr
  val tabEstoqueGeralViewModel: ITabEstoqueGeralViewModel
}

