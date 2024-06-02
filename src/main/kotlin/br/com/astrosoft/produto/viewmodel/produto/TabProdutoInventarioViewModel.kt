package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroProdutoInventario
import br.com.astrosoft.produto.model.beans.Loja
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
    val selecionado = subView.produtosSelecionados()
    val produto = selecionado.firstOrNull() ?: fail("Nenhum produto selecionado")
    val novo = ProdutoInventario(
      loja = produto.loja,
      lojaAbrev = produto.lojaAbrev,
      prdno = produto.prdno,
      codigo = produto.codigo,
      descricao = produto.descricao,
      grade = produto.grade,
      unidade = produto.unidade,
      validade = produto.validade,
      vendno = produto.vendno,
      fornecedorAbrev = produto.fornecedorAbrev,
      estoqueTotal = produto.estoqueTotal,
      estoque = null,
      vencimento = null,
      dataEntrada = LocalDate.now(),
      saidaVenda = null,
      saidaTransf = null,
      entradaTransf = null,
      entradaCompra = null
    )
    subView.formAdd(novo) { novoEditado ->
      novoEditado.update()
      updateView()
    }
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

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  val subView
    get() = viewModel.view.tabProdutoInventario
}

interface ITabProdutoInventario : ITabView {
  fun filtro(): FiltroProdutoInventario
  fun updateProdutos(produtos: List<ProdutoInventario>)
  fun produtosSelecionados(): List<ProdutoInventario>
  fun formAdd(produtoInicial: ProdutoInventario, callback: (novoEditado: ProdutoInventario) -> Unit)
}