package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.ETipo
import br.com.astrosoft.produto.model.beans.FiltroProdutoInventario
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoInventario
import java.time.LocalDate

class TabProdutoInventarioAgrupadoViewModel(val viewModel: ProdutoViewModel) {

  fun updateView() = viewModel.exec {
    subView.execThread {
      val filtro = subView.filtro()
      val produtos = ProdutoInventario.findAgrupado(filtro)
      subView.updateProdutos(produtos)
    }
  }

  fun salvaInventario(bean: ProdutoInventario?) {
    subView.execThread {
      bean?.update()
      updateView()
    }
  }

  fun adicionarLinha() = viewModel.exec {
    subView.execThread {
      val selecionado = subView.produtosSelecionados()
      val produto = selecionado.firstOrNull() ?: fail("Nenhum produto selecionado")
      val novo = produto.copy {
        movimento = null
        vencimento = null
        vencimentoEdit = null
        dataEntrada = LocalDate.now()
        dataEntradaEdit = LocalDate.now()
        tipoEdit = null
        tipo = ETipo.INV.tipo
      }
      subView.formAdd(novo) { novoEditado ->
        novoEditado.update()
        updateView()
      }
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

  fun atualizarTabelas() {
    ProdutoInventario.atualizaTabelas()
    updateView()
  }

  val subView
    get() = viewModel.view.tabProdutoInventarioAgrupado
}

interface ITabProdutoInventarioAgrupado : ITabView {
  fun filtro(): FiltroProdutoInventario
  fun updateProdutos(produtos: List<ProdutoInventario>)
  fun produtosSelecionados(): List<ProdutoInventario>
  fun formAdd(produtoInicial: ProdutoInventario, callback: (novoEditado: ProdutoInventario) -> Unit)
}