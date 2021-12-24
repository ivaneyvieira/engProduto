package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroProduto
import br.com.astrosoft.produto.model.beans.Produto

class TabProdutoListViewModel(val viewModel: ProdutoViewModel) {
  fun updateView()= viewModel.exec {
    val filtro = subView.filtro()
    val produtos = Produto.find(filtro)
    subView.updateProdutos(produtos)
  }

  val subView
    get() = viewModel.view.tabProdutoList
}

interface ITabProdutoList : ITabView{
  fun filtro() : FiltroProduto
  fun updateProdutos(produtos: List<Produto>)
}