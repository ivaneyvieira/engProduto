package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroProdutoInventario
import br.com.astrosoft.produto.model.beans.ProdutoInventario
import java.time.LocalDate

class TabProdutoInventarioViewModel(val viewModel: ProdutoViewModel) {

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoInventario.find(filtro)
    subView.updateProdutos(produtos)
  }

  fun salvaInventario(bean: ProdutoInventario?) {
    bean?.update()
    updateView()
  }

  fun adicionarLinha() = viewModel.exec {
    val selecionado = subView.produtosSelecionados().ifEmpty {
      fail("Nenhum produto selecionado")
    }
    selecionado.forEach { produto ->
      val novo = ProdutoInventario(
        prdno = produto.prdno,
        codigo = produto.codigo,
        descricao = produto.descricao,
        grade = produto.grade,
        unidade = produto.unidade,
        validade = produto.validade,
        vendno = produto.vendno,
        fornecedorAbrev = produto.fornecedorAbrev,
        estoqueTotal = produto.estoqueTotal,
        estoqueTotalDS = produto.estoqueTotalDS,
        estoqueTotalMR = produto.estoqueTotalMR,
        estoqueTotalMF = produto.estoqueTotalMF,
        estoqueTotalPK = produto.estoqueTotalPK,
        estoqueTotalTM = produto.estoqueTotalTM,
        seq = 0,
        estoqueDS = null,
        estoqueMR = null,
        estoqueMF = null,
        estoquePK = null,
        estoqueTM = null,
        vencimentoDS = null,
        vencimentoMR = null,
        vencimentoMF = null,
        vencimentoPK = null,
        vencimentoTM = null,
        dataEntrada = LocalDate.now(),
      )
      novo.update()
    }
    updateView()
  }

  fun removerLinha() = viewModel.exec {
    val selecionado = subView.produtosSelecionados().ifEmpty {
      fail("Nenhum produto selecionado")
    }
    viewModel.view.showQuestion("Confirma a exclusão dos produtos selecionados?") {
      selecionado.forEach { produto ->
        produto.remove()
      }
      updateView()
    }
  }

  val subView
    get() = viewModel.view.tabProdutoInventario
}

interface ITabProdutoInventario : ITabView {
  fun filtro(): FiltroProdutoInventario
  fun updateProdutos(produtos: List<ProdutoInventario>)
  fun produtosSelecionados(): List<ProdutoInventario>
}