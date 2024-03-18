package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabReposicaoSepViewModel(val viewModel: ReposicaoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val reposicoes = reposicoes()
    subView.updateReposicoes(reposicoes)
  }

  private fun reposicoes(): List<Reposicao> {
    val filtro = subView.filtro()
    val reposicoes = Reposicao.findAll(filtro)
    return reposicoes
  }

  fun selecionaProdutos(codigoBarra: String?) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.selecionado = EMarcaReposicao.ENT.num
    produto.salva()
    subView.updateProduto(produto)
  }

  fun marca() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter { it.selecionado == EMarcaReposicao.ENT.num }
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }

    itens.forEach { produto ->
      produto.marca = EMarcaReposicao.ENT.num
      produto.selecionado = EMarcaReposicao.ENT.num
      produto.qtRecebido = produto.quantidade
      produto.salva()
    }
    updateProdutos()
  }

  fun desmarcar() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter { it.selecionado == EMarcaReposicao.ENT.num }
    itens.ifEmpty {
      fail("Nenhum produto para desmarcar")
    }

    itens.forEach { produto ->
      produto.selecionado = EMarcaReposicao.SEP.num
      produto.salva()
    }
    updateProdutos()
  }

  fun saveQuant(bean: ReposicaoProduto) {
    bean.salva()
    updateProdutos()
  }

  fun updateProdutos() {
    val reposicoes = reposicoes()
    subView.updateReposicoes(reposicoes)
    subView.updateProdutos(reposicoes)
  }

  fun salva(bean: Reposicao) {
    bean.salva()
    updateView()
  }

  val subView
    get() = viewModel.view.tabReposicaoSep
}

interface ITabReposicaoSep : ITabView {
  fun filtro(): FiltroReposicao
  fun updateReposicoes(reposicoes: List<Reposicao>)
  fun produtosCodigoBarras(codigoBarra: String?): ReposicaoProduto?
  fun produtosSelecionados(): List<ReposicaoProduto>
  fun updateProduto(produto: ReposicaoProduto)
  fun updateProdutos(reposicoes: List<Reposicao>)
}