package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.EtiquetaChave
import java.time.LocalDate
import java.time.LocalTime

class TabRessuprimentoCDViewModel(val viewModel: RessuprimentoViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaRessuprimento.CD)
    val resuprimento = Ressuprimento.find(filtro)
    subView.updateRessuprimentos(resuprimento)
  }

  fun marcaEnt() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter { it.selecionado == true }
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }

    itens.forEach { produto ->
      produto.marca = EMarcaRessuprimento.ENT.num
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun selecionaProdutos(codigoBarra: String) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.selecionado = true
    produto.salva()

    subView.updateProduto(produto)
  }

  fun salvaProdutos() = viewModel.exec {
    val itens = subView.produtosMarcados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
    val usuario = AppConfig.userLogin()?.login ?: ""
    itens.forEach { produto ->
      produto.salva()
    }

    imprimeEtiquetaEnt(itens)
    subView.updateProdutos()
  }

  private fun imprimeEtiquetaEnt(produtos: List<ProdutoRessuprimento>) {
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, produtos)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  fun desmarcar() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter { it.selecionado == true }
    itens.ifEmpty {
      fail("Nenhum produto para desmarcar")
    }

    itens.forEach { produto ->
      produto.selecionado = false
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
      var count = 0
      lista.forEach { ressuprimento ->
        count += ressuprimento.exclui()
      }
      if (count == 0) {
        viewModel.view.showWarning("Nenhum ressuprimento excluído")
      }
      updateView()
    }
  }

  fun saveQuant(bean : ProdutoRessuprimento) {
    bean.salva()
    subView.updateProdutos()
  }

  val subView
    get() = viewModel.view.tabRessuprimentoCD
}

interface ITabRessuprimentoCD : ITabView {
  fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento
  fun updateRessuprimentos(ressuprimentos: List<Ressuprimento>)
  fun updateProdutos()
  fun produtosSelecionados(): List<ProdutoRessuprimento>
  fun produtosMarcados(): List<ProdutoRessuprimento>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento?
  fun findRessuprimento(): Ressuprimento?
  fun updateProduto(produto: ProdutoRessuprimento)
  fun itensSelecionados(): List<Ressuprimento>
}