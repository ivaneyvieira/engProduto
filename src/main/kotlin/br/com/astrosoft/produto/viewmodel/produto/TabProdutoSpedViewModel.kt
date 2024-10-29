package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroProdutoSped
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoSped
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoSped

class TabProdutoSpedViewModel(val viewModel: ProdutoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    //subView.execThread {
    val filtro = subView.filtro()
    val produtos = ProdutoSped.find(filtro)

    subView.updateProdutos(produtos)
    // }
  }

  fun configProdutosSelecionados() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    val prdConf = produtos.any { (it.ctLoja ?: 0) > 0 }
    if (prdConf) {
      viewModel.view.showQuestion("Deseja reconfigurar os produtos selecionados?") {
        produtos.forEach { it.updateSt() }
        updateView()
      }
    } else {
      produtos.forEach { it.updateSt() }
      updateView()
    }
  }

  fun planilha(produtos: List<ProdutoSped>): ByteArray {
    val planilha = PlanilhaProdutoSped()
    return planilha.write(produtos)
  }

  val subView
    get() = viewModel.view.tabProdutoSped
}

interface ITabProdutoSped : ITabView {
  fun filtro(): FiltroProdutoSped
  fun updateProdutos(produtos: List<ProdutoSped>)
  fun produtosSelecionados(): List<ProdutoSped>
}