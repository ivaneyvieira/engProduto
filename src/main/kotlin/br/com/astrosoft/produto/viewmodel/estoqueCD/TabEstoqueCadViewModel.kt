package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaMovManual
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoque

class TabEstoqueCadViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueCad

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoEstoque.findProdutoEstoque(filtro)
    subView.updateProduto(produtos)
  }

  fun geraPlanilha(produtos: List<ProdutoEstoque>): ByteArray {
    val planilha = PlanilhaProdutoEstoque()
    return planilha.write(produtos)
  }

  fun updateProduto(bean: ProdutoEstoque?) = viewModel.exec{
    bean ?: fail("Produto não informado")
    bean.update()
    updateView()
  }

  fun copiaLocalizacao() = viewModel.exec {
    val itens = subView.itensSelecionados()
    if(itens.isEmpty()) fail("Nenhum item selecionado")

    val primeiro = itens.firstOrNull() ?: fail("Nenhum item selecionado")
    itens.forEach {item ->
      item.locApp = primeiro.locApp
      item.update()
    }
    updateView()
  }
}

interface ITabEstoqueCad : ITabView {
  fun filtro(): FiltroProdutoEstoque
  fun updateProduto(produtos: List<ProdutoEstoque>)
  fun itensSelecionados(): List<ProdutoEstoque>
}
