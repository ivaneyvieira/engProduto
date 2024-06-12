package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.DadosValidade
import br.com.astrosoft.produto.model.beans.FiltroDadosValidade
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoInventario

class TabDadosValidadeViewModel(val viewModel: ProdutoViewModel) {

  fun updateView() = viewModel.exec {
    subView.execThread {
      val filtro = subView.filtro()
      val produtos = DadosValidade.findAll(filtro)
      subView.updateProdutos(produtos)
    }
  }

  fun salvaInventario(bean: DadosValidade?) = viewModel.exec {
    bean ?: fail("Nenhum produto selecionado")
    bean.update()
    updateView()
  }

  fun adicionarLinha() = viewModel.exec {
    val selecionado = subView.produtosSelecionados()
    if (selecionado.isEmpty()) {
      val filtro = subView.filtro()
      if (filtro.codigo == "") {
        fail("Informe o código do produto")
      }

      val result = DadosValidade.insert(filtro.storeno, filtro.codigo, "")
      if (result == 0) fail("Produto não encontrado")
    } else {
      selecionado.forEach {
        DadosValidade.insert(it.loja, it.codigo.toString(), it.grade)
      }
    }
    updateView()
  }

  fun removerLinha() = viewModel.exec {
    val selecionado = subView.produtosSelecionados().ifEmpty {
      fail("Nenhum produto selecionado")
    }
    viewModel.view.showQuestion("Confirma a exclusão dos produtos selecionados?") {
      selecionado.forEach { produto ->
        produto.delete()
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
    get() = viewModel.view.tabDadosValidade
}

interface ITabDadosValidade : ITabView {
  fun filtro(): FiltroDadosValidade
  fun updateProdutos(produtos: List<DadosValidade>)
  fun produtosSelecionados(): List<DadosValidade>
}