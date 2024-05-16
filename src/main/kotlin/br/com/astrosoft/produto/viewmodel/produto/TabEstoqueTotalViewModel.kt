package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.produto.model.beans.FiltroListaProduto
import br.com.astrosoft.produto.model.beans.Produtos

class TabEstoqueTotalViewModel(viewModel: ProdutoViewModel) :
  TabAbstractProdutoViewModel<ITabEstoqueTotalViewModel>(viewModel) {
  override val subView
    get() = viewModel.view.tabEstoqueTotalViewModel

  override fun findPrecoAlteracao(filtro: FiltroListaProduto): List<Produtos> {
    return Produtos.find(filtro, false)
  }

  override fun todoEstoque(): Boolean {
    return true
  }
}

interface ITabEstoqueTotalViewModel : ITabAbstractProdutoViewModel