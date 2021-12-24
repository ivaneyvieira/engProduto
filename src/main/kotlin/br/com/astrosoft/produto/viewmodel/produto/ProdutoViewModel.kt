package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ProdutoViewModel(view: IProdutoView) : ViewModel<IProdutoView>(view) {
  val tabProdutoListViewModel = TabProdutoRetiraEntregaViewModel(this)

  override fun listTab() = listOf(
    view.tabProdutoList,
                                 )
}

interface IProdutoView : IView {
  val tabProdutoList: ITabProdutoRetiraEntrega
}

