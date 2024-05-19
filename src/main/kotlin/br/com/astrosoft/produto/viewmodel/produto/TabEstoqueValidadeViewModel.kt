package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.produto.model.beans.FiltroListaProduto
import br.com.astrosoft.produto.model.beans.Produtos

class TabEstoqueValidadeViewModel(viewModel: ProdutoViewModel) :
  TabAbstractProdutoViewModel<ITabEstoqueValidadeViewModel>(viewModel) {
  override val subView
    get() = viewModel.view.tabEstoqueValidadeViewModel

  override fun findPrecoAlteracao(filtro: FiltroListaProduto): List<Produtos> {
    return Produtos.find(filtro, false)
  }
}

interface ITabEstoqueValidadeViewModel : ITabAbstractProdutoViewModel