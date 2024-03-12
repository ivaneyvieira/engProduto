package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabRessuprimentoCDViewModel(val viewModel: RessuprimentoViewModel) {
    fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro(EMarcaRessuprimento.CD)
    val resuprimento = Ressuprimento.find(filtro)
    subView.updateRessuprimentos(resuprimento)
  }

  fun marca() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter { it.selecionado == EMarcaRessuprimento.ENT.num }
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }

    itens.forEach { produto ->
      produto.marca = EMarcaRessuprimento.ENT.num
      produto.selecionado = EMarcaRessuprimento.ENT.num
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun selecionaProdutos(codigoBarra: String) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.selecionado = EMarcaRessuprimento.ENT.num
    produto.salva()
    subView.updateProduto(produto)
  }

  fun desmarcar() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter { it.selecionado == EMarcaRessuprimento.ENT.num }
    itens.ifEmpty {
      fail("Nenhum produto para desmarcar")
    }

    itens.forEach { produto ->
      produto.selecionado = EMarcaRessuprimento.CD.num
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun excluiRessuprimento() = viewModel.exec {
    val lista = subView.itensSelecionados()
    if (lista.isEmpty()) {
      fail("Nenhum ressuprimento selecionado")
    }
    viewModel.view.showQuestion("Tem certeza que deseja excluir o ressuprimento selecionado?") {
      lista.forEach { ressuprimento ->
        ressuprimento.exclui()
      }
      updateView()
    }
  }

  fun saveQuant(bean: ProdutoRessuprimento) {
    bean.salva()
    subView.updateProdutos()
  }

  fun saveObservacao(bean: Ressuprimento?) = viewModel.exec {
    bean ?: fail("Nenhum ressuprimento selecionado")
    bean.salva()
    updateView()
  }

  val subView
    get() = viewModel.view.tabRessuprimentoCD
}

interface ITabRessuprimentoCD : ITabView {
  fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento
  fun updateRessuprimentos(ressuprimentos: List<Ressuprimento>)
  fun updateProdutos()
  fun produtosSelecionados(): List<ProdutoRessuprimento>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento?
  fun findRessuprimento(): Ressuprimento?
  fun updateProduto(produto: ProdutoRessuprimento)
  fun itensSelecionados(): List<Ressuprimento>
}