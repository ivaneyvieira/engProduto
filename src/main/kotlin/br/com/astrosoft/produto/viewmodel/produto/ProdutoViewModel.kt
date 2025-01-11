package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ProdutoViewModel(view: IProdutoView) : ViewModel<IProdutoView>(view) {
  val tabProdutoListViewModel = TabProdutoListViewModel(this)
  val tabProdutoCadastroViewModel = TabProdutoCadastroViewModel(this)
  val tabProdutoSpedViewModel = TabProdutoSpedViewModel(this)
  val tabProdutoInventarioViewModel = TabProdutoInventarioViewModel(this)
  val tabProdutoInventarioAgrupadoViewModel = TabProdutoInventarioAgrupadoViewModel(this)
  val tabProdutoUsrViewModel = TabProdutoUsrViewModel(this)
  val tabEstoqueGiroViewModel = TabEstoqueGiroViewModel(this)
  val tabDadosValidadeViewModel = TabDadosValidadeViewModel(this)
  val tabEstoqueValidadeViewModel = TabEstoqueValidadeViewModel(this)
  val tabEstoqueValidadeLojaViewModel = TabEstoqueValidadeLojaViewModel(this)

  override fun listTab() = listOf(
    view.tabProdutoList,
    view.tabProdutoCadastro,
    view.tabProdutoSped,
    view.tabEstoqueGiroViewModel,
    view.tabEstoqueValidadeViewModel,
    view.tabEstoqueValidadeLojaViewModel,
    view.tabProdutoInventario,
    view.tabProdutoInventarioAgrupado,
    view.tabDadosValidade,
    view.tabProdutoUsr,
  )
}

interface IProdutoView : IView {
  val tabProdutoList: ITabProdutoList
  val tabProdutoCadastro: ITabProdutoCadastro
  val tabProdutoSped: ITabProdutoSped
  val tabProdutoInventario: ITabProdutoInventario
  val tabProdutoInventarioAgrupado: ITabProdutoInventarioAgrupado
  val tabProdutoUsr: ITabProdutoUsr
  val tabDadosValidade: ITabDadosValidade
  val tabEstoqueGiroViewModel: ITabEstoqueGiroViewModel
  val tabEstoqueValidadeViewModel: ITabEstoqueValidadeViewModel
  val tabEstoqueValidadeLojaViewModel: ITabEstoqueValidadeLojaViewModel
}

