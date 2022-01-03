package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ProdutoViewModel(view: IProdutoView) : ViewModel<IProdutoView>(view) {
  val tabProdutoRetiraEntregaViewModel = TabProdutoRetiraEntregaViewModel(this)
  val tabProdutoRetiraEntregaEditViewModel = TabProdutoRetiraEntregaEditViewModel(this)
  val tabProdutoListViewModel = TabProdutoListViewModel(this)
  val tabProdutoReservaViewModel = TabProdutoReservaViewModel(this)

  override fun listTab() = listOf(
    view.tabProdutoList, view.tabProdutoRetiraEntrega, view.tabProdutoRetiraEntregaEdit, view.tabProdutoReserva
                                 )
}

interface IProdutoView : IView {
  val tabProdutoRetiraEntrega: ITabProdutoRetiraEntrega
  val tabProdutoRetiraEntregaEdit: ITabProdutoRetiraEntregaEdit
  val tabProdutoList: ITabProdutoList
  val tabProdutoReserva: ITabProdutoReserva
}

