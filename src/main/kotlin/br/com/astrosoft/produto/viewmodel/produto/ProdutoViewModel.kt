package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ProdutoViewModel(view: IProdutoView) : ViewModel<IProdutoView>(view) {
  val tabProdutoRetiraEntregaViewModel = TabProdutoRetiraEntregaViewModel(this)
  val tabProdutoListViewModel = TabProdutoListViewModel(this)
  val tabProdutoReservaViewModel = TabProdutoReservaViewModel(this)

  override fun listTab() = listOf(
    view.tabProdutoList, view.tabProdutoRetiraEntrega, view.tabProdutoReserva
                                 )
}

interface IProdutoView : IView {
  val tabProdutoRetiraEntrega: ITabProdutoRetiraEntrega
  val tabProdutoList: ITabProdutoList
  val tabProdutoReserva: ITabProdutoReserva
}

