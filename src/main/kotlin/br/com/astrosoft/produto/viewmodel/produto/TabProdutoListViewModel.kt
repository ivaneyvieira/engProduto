package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView


class TabProdutoListViewModel(val viewModel: ProdutoViewModel) {
  fun updateView() {
    TODO("Not yet implemented")
  }

  val subView
    get() = viewModel.view.tabProdutoList
}

interface ITabProdutoList : ITabView