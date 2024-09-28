package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.produto.model.beans.FiltroListaProduto
import br.com.astrosoft.produto.model.beans.Produtos

class TabEstoqueGiroViewModel(viewModel: ProdutoViewModel) :
  TabAbstractProdutoViewModel<ITabEstoqueGiroViewModel>(viewModel) {
  override val subView
    get() = viewModel.view.tabEstoqueGiroViewModel

  override fun findPrecoAlteracao(filtro: FiltroListaProduto): List<Produtos> {
    return Produtos.find(filtro, false)
  }
}

interface ITabEstoqueGiroViewModel : ITabAbstractProdutoViewModel

//TODO usa localizacao saci