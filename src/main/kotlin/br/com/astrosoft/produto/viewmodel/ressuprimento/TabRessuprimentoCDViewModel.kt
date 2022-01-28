package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.model.Config
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
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produto ->
      produto.marca = EMarcaRessuprimento.ENT.num
      val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
      val usuario = Config.user?.login ?: ""
      produto.usuarioCD = "$usuario-$dataHora"
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun marcaEntProdutos(codigoBarra: String) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.marca = EMarcaRessuprimento.ENT.num
    val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
    val usuario = Config.user?.login ?: ""
    produto.usuarioCD = "$usuario-$dataHora"
    subView.updateProduto(produto)
  }

  fun salvaProdutos() = viewModel.exec {
    val itens = subView.produtosMarcados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
    val usuario = Config.user?.login ?: ""
    itens.forEach { produto ->
      produto.usuarioCD = "$usuario-$dataHora"
      produto.salva()
    }
    val ressuprimento = subView.findRessuprimento() ?: fail("Nota não encontrada")
    imprimeEtiquetaEnt(ressuprimento)
    subView.updateProdutos()
  }

  private fun imprimeEtiquetaEnt(ressuprimento: Ressuprimento) {
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, ressuprimento)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  val subView
    get() = viewModel.view.tabRessuprimentoCD
}

interface ITabRessuprimentoCD : ITabView {
  fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento
  fun updateRessuprimentos(ressuprimentos: List<Ressuprimento>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoRessuprimento>
  fun produtosMarcados(): List<ProdutoRessuprimento>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento?
  fun findRessuprimento(): Ressuprimento?
  fun updateProduto(produto: ProdutoRessuprimento)
}