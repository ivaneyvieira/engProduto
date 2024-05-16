package br.com.astrosoft.promocao.viewmodel.produto

import br.com.astrosoft.produto.model.beans.FiltroListaProduto
import br.com.astrosoft.produto.model.beans.Produtos
import br.com.astrosoft.produto.viewmodel.produto.ProdutoViewModel

class TabEstoqueGeralViewModel(viewModel: ProdutoViewModel) :
  TabAbstractProdutoViewModel<ITabEstoqueGeralViewModel>(viewModel) {
  override val subView
    get() = viewModel.view.tabEstoqueGeralViewModel

  override fun findPrecoAlteracao(filtro: FiltroListaProduto): List<Produtos> {
    return Produtos.find(filtro, false)
  }

  override fun todoEstoque(): Boolean {
    return true
  }
}

interface ITabEstoqueGeralViewModel : ITabAbstractProdutoViewModel