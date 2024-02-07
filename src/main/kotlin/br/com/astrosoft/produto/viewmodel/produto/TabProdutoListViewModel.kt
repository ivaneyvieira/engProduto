package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroProdutoSaldo
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoSaldo
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoSaldo

class TabProdutoListViewModel(val viewModel: ProdutoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoSaldo.findProdutoSaldo(filtro)
    subView.updateProdutos(produtos)
  }

  fun geraPlanilha(produtos: List<ProdutoSaldo>): ByteArray {
    val planilha = PlanilhaProdutoSaldo()
    return planilha.write(produtos)
  }

  val subView
    get() = viewModel.view.tabProdutoList
}

interface ITabProdutoList : ITabView {
  fun filtro(): FiltroProdutoSaldo
  fun updateProdutos(produtos: List<ProdutoSaldo>)
}