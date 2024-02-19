package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*

class TabEstoqueMFViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueMF

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoEstoque.findProdutoEstoque(filtro)
    subView.updateProduto(produtos)
  }
}

interface ITabEstoqueMF : ITabView {
  fun filtro(): FiltroProdutoEstoque
  fun updateProduto(produtos: List<ProdutoEstoque>)
}
