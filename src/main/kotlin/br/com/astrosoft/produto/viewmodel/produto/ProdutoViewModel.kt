package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ProdutoViewModel(view: IProdutoView) : ViewModel<IProdutoView>(view) {
  val tabProdutoListViewModel = TabProdutoListViewModel(this)
  val tabProdutoInventarioViewModel = TabProdutoInventarioViewModel(this)
  val tabProdutoInventarioAgrupadoViewModel = TabProdutoInventarioAgrupadoViewModel(this)
  val tabProdutoUsrViewModel = TabProdutoUsrViewModel(this)
  val tabEstoqueGiroViewModel = TabEstoqueGiroViewModel(this)
  val tabDadosValidadeViewModel = TabDadosValidadeViewModel(this)
  val tabEstoqueValidadeViewModel = TabEstoqueValidadeViewModel(this)

  override fun listTab() = listOf(
    view.tabProdutoList,
    view.tabEstoqueGiroViewModel,
    view.tabEstoqueValidadeViewModel,
    view.tabProdutoInventario,
    view.tabProdutoInventarioAgrupado,
    view.tabDadosValidade,
    view.tabProdutoUsr,
  )
}

interface IProdutoView : IView {
  val tabProdutoList: ITabProdutoList
  val tabProdutoInventario: ITabProdutoInventario
  val tabProdutoInventarioAgrupado: ITabProdutoInventarioAgrupado
  val tabProdutoUsr: ITabProdutoUsr
  val tabDadosValidade: ITabDadosValidade
  val tabEstoqueGiroViewModel: ITabEstoqueGiroViewModel
  val tabEstoqueValidadeViewModel: ITabEstoqueValidadeViewModel
}

