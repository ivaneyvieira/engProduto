package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroProduto
import br.com.astrosoft.produto.model.beans.Produto
import br.com.astrosoft.produto.model.beans.ProdutoReserva

class TabProdutoReservaViewModel(val viewModel: ProdutoViewModel) {
  fun updateView()= viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoReserva.find(filtro)
    subView.updateProdutos(produtos)
  }

  val subView
    get() = viewModel.view.tabProdutoReserva
}

interface ITabProdutoReserva : ITabView{
  fun filtro() : FiltroProduto
  fun updateProdutos(produtos: List<ProdutoReserva>)
}