package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*

class TabProdutoInventarioViewModel(val viewModel: ProdutoViewModel) {

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoInventario.find(filtro)
    subView.updateProdutos(produtos)
  }

  val subView
    get() = viewModel.view.tabProdutoInventario
}

interface ITabProdutoInventario : ITabView {
  fun filtro(): FiltroProdutoInventario
  fun updateProdutos(produtos: List<ProdutoInventario>)
  fun produtosSelecionados(): List<ProdutoInventario>
}