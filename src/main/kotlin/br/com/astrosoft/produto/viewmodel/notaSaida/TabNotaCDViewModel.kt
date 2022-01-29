package br.com.astrosoft.produto.viewmodel.notaSaida

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.EtiquetaChave
import java.time.LocalDate
import java.time.LocalTime

class TabNotaCDViewModel(val viewModel: NotaViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaNota.CD)
    val notas = NotaSaida.find(filtro)
    subView.updateNotas(notas)
  }

  fun marcaExp() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaNota.EXP.num
      produtoNF.usuarioExp = ""
      produtoNF.usuarioCD = ""
      produtoNF.salva()
    }
    subView.updateProdutos()
    updateView()
  }

  fun marcaEnt() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaNota.ENT.num
      val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
      val usuario = Config.user?.login ?: ""
      produtoNF.usuarioCD = "$usuario-$dataHora"
      produtoNF.salva()
    }
    subView.updateProdutos()
    updateView()
  }

  fun marcaEntProdutos(codigoBarra: String) = viewModel.exec {
    val produtoNF = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produtoNF.marca = EMarcaNota.ENT.num
    val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
    val usuario = Config.user?.login ?: ""
    produtoNF.usuarioCD = "$usuario-$dataHora"
    produtoNF.salva()
    subView.updateProdutos()
    val nota = subView.findNota() ?: fail("Nota não encontrada")
    val produtosRestantes = nota.produtos(EMarcaNota.CD)
    if (produtosRestantes.isEmpty()) {
      imprimeEtiquetaEnt(nota.produtos(EMarcaNota.ENT))
    }
  }

  private fun imprimeEtiquetaEnt(produtos: List<ProdutoNFS>) {
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, produtos)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  fun printEtiquetaExp(nota: NotaSaida?) = viewModel.exec {
    nota ?: fail("Nenhuma notaSaida selecionada")
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreviewExp(impressora, nota.produtos(EMarcaNota.CD))
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  fun findGrade(prd: ProdutoNFS?, block: (List<PrdGrade>) -> Unit) = viewModel.exec {
    prd ?: return@exec
    val list = prd.findGrades()
    block(list)
  }

  val subView
    get() = viewModel.view.tabNotaCD
}

interface ITabNotaCD : ITabView {
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNFS>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoNFS?
  fun findNota(): NotaSaida?
}