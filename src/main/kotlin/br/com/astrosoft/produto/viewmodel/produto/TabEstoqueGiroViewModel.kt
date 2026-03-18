package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.produto.model.beans.EEstoqueList
import br.com.astrosoft.produto.model.beans.FiltroListaProduto
import br.com.astrosoft.produto.model.beans.Produtos

class TabEstoqueGiroViewModel(viewModel: ProdutoViewModel) :
  TabAbstractProdutoViewModel<ITabEstoqueGiroViewModel>(viewModel) {
  override val subView
    get() = viewModel.view.tabEstoqueGiroViewModel

  override fun findPrecoAlteracao(filtro: FiltroListaProduto): List<Produtos> {
    return Produtos.find(filtro, false).filter { produto ->
      val vendas = produto.qttyVendas ?: 0
      when(subView.filtroVendas()){
        EEstoqueList.MENOR -> vendas <= subView.vendas()
        EEstoqueList.MAIOR -> vendas >= subView.vendas()
        EEstoqueList.IGUAL -> vendas == subView.vendas()
        EEstoqueList.TODOS -> true
      }
    }
  }
}

interface ITabEstoqueGiroViewModel : ITabAbstractProdutoViewModel {
  fun filtroVendas(): EEstoqueList
  fun vendas(): Int
}
