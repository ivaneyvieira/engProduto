package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroProdutoCadastro
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoCadastro

class TabProdutoCadastroViewModel(val viewModel: ProdutoViewModel) {
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
    val produtos = ProdutoCadastro.find(filtro)

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

  val subView
    get() = viewModel.view.tabProdutoCadastro
}

interface ITabProdutoCadastro : ITabView {
  fun filtro(): FiltroProdutoCadastro
  fun updateProdutos(produtos: List<ProdutoCadastro>)
  fun produtosSelecionados(): List<ProdutoCadastro>
}