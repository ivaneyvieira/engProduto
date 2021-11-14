package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView


class TabProdutoListViewModel(val viewModel: ProdutoViewModel) {
  val subView
    get() = viewModel.view.tabProdutoList
}

interface ITabProdutoList : ITabView