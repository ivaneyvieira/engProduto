package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroListaProduto
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.Produtos
import br.com.astrosoft.produto.model.report.ProdutoRelatorio
import br.com.astrosoft.produto.model.report.RelatorioProduto

class TabEstoqueValidadeLojaViewModel(val viewModel: ProdutoViewModel) {
  private val subView
    get() = viewModel.view.tabEstoqueValidadeLojaViewModel

  fun findPrecoAlteracao(filtro: FiltroListaProduto): List<Produtos> {
    return Produtos.findLoja(filtro, false)
  }

  fun updateView() {
    val filtro = subView.filtro()
    val resultList = findPrecoAlteracao(filtro)
    subView.updateGrid(resultList)
  }

  fun allLojas() = Loja.allLojas()
  fun geraRelatorio() {
    val produtos = subView.produtosSelecionados()
    val filtro = subView.filtro()
    val report = RelatorioProduto(filtro.loja)
    val listaProduto = produtos.map {
      ProdutoRelatorio(
        codigo = it.codigo ?: 0,
        descricao = it.descricao ?: "",
        grade = it.grade ?: "",
        unidade = it.unidade ?: "",
        quant = when (filtro.loja) {
          0 -> it.estoque
          2 -> it.DS_TT
          3 -> it.MR_TT
          4 -> it.MF_TT
          5 -> it.PK_TT
          8 -> it.TM_TT
          else -> 0
        } ?: 0
      )
    }
    val file = report.processaRelatorio(listaProduto)
    viewModel.view.showReport(chave = "NotaImpresso${System.nanoTime()}", report = file)
  }

  fun salvaValidades(produto: Produtos) {
    produto.updateValidades(produto.storeno ?: 0)
    produto.processaVendas()
    updateView()
  }

  fun processaVendas() = viewModel.exec {
    val produto = subView.produtosSelecionados().ifEmpty {
      fail("Nenhum produto selecionado")
    }

    produto.forEach { it.processaVendas() }

    updateView()
  }
}

interface ITabEstoqueValidadeLojaViewModel : ITabView {
  fun filtro(): FiltroListaProduto
  fun updateGrid(itens: List<Produtos>)
  fun produtosSelecionados(): List<Produtos>
}