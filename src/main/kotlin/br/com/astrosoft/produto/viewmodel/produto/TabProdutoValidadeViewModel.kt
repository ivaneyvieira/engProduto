package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*

class TabProdutoValidadeViewModel(val viewModel: ProdutoViewModel) {

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoValidade.find(filtro)
    subView.updateProdutos(produtos)
  }

  val subView
    get() = viewModel.view.tabProdutoValidade
}

interface ITabProdutoValidade : ITabView {
  fun filtro(): FiltroProdutoValidade
  fun updateProdutos(produtos: List<ProdutoValidade>)
  fun produtosSelecionados(): List<ProdutoValidade>
}