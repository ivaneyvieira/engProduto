package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoSaldo

class TabProdutoCadastroViewModel(val viewModel: ProdutoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    subView.execThread {
      val filtro = subView.filtro()
      val produtos = ProdutoCadastro.find(filtro)

      subView.updateProdutos(produtos)
    }
  }

  val subView
    get() = viewModel.view.tabProdutoCadastro
}

interface ITabProdutoCadastro : ITabView {
  fun filtro(): FiltroProdutoCadastro
  fun updateProdutos(produtos: List<ProdutoCadastro>)
  fun produtosSelecionados(): List<ProdutoCadastro>
}