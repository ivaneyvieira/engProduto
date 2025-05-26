package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoqueAcerto
import br.com.astrosoft.produto.model.printText.PrintProdutosConferenciaAcerto
import br.com.astrosoft.produto.model.printText.PrintProdutosConferenciaEstoque
import br.com.astrosoft.produto.model.report.ReportAcerto
import br.com.astrosoft.produto.model.saci

class TabEstoqueAcertoViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueAcerto

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoEstoqueAcerto.findAll(filtro).agrupa().sortedBy { it.numero }
    subView.updateProduto(produtos)
  }

  fun imprimirPedido(acerto: EstoqueAcerto) = viewModel.exec {
    val produtos = acerto.findProdutos()

    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val report = PrintProdutosConferenciaEstoque("Pedido de acerto: ${acerto.numero}")

    report.print(
      dados = produtos, printer = subView.printerPreview()
    )
  }

  fun imprimirAcerto(acerto: EstoqueAcerto) = viewModel.exec {
    val produtos = acerto.findProdutos().filter {
      (it.diferencaAcerto ?: 0) != 0
    }.sortedBy { it.diferencaAcerto ?: 999999 }
    if (produtos.isEmpty()) {
      fail("Nenhum produto válido selecionado")
    }

    val report = PrintProdutosConferenciaAcerto()

    report.print(
      dados = produtos, printer = subView.printerPreview()
    )
  }

  fun cancelarAcerto() = viewModel.exec {
    val itensSelecionado = subView.itensSelecionados().filter {
      it.processado == false
    }
    if (itensSelecionado.isEmpty()) {
      fail("Nenhum acerto não processado selecionado")
    }
    viewModel.view.showQuestion("Confirma o cancelamento do acerto selecionado?") {
      itensSelecionado.forEach {
        it.cancela()
      }
      updateView()
    }
  }

  fun imprimirRelatorio(acerto: EstoqueAcerto) {
    val produtos = acerto.findProdutos()
    val report = ReportAcerto()
    val file = report.processaRelatorio(produtos)
    viewModel.view.showReport(chave = "Acerto${System.nanoTime()}", report = file)
  }

  fun geraPlanilha(produtos: List<ProdutoEstoqueAcerto>): ByteArray {
    val planilha = PlanilhaProdutoEstoqueAcerto()
    return planilha.write(produtos)
  }

  fun updateProduto(produto: ProdutoEstoqueAcerto) = viewModel.exec {
    produto.save()
  }

  fun gravaAcerto(acerto: EstoqueAcerto) = viewModel.exec {
    if (acerto.gravado == true) {
      fail("Acerto já gravado")
    }
    subView.autorizaAcerto { user ->
      val pordutos = acerto.findProdutos()
      pordutos.forEach {
        it.gravadoLogin = user.no
        it.gravado = true
        it.save()
      }
      updateView()
    }
  }

  fun removeAcerto() = viewModel.exec {
    val itensSelecionado = subView.produtosSelecionado()

    itensSelecionado.ifEmpty {
      fail("Nenhum acerto selecionado")
    }

    if (itensSelecionado.any { it.processado == true }) {
      fail("Acerto está processado")
    }

    subView.autorizaAcerto {
      itensSelecionado.forEach { produto ->
        produto.remove()
      }
      updateView()
    }
  }

  fun addProduto(produto: ProdutoEstoqueAcerto) {
    produto.save()
    updateView()
  }

  fun findProdutos(codigo: String, loja: Int): List<PrdGrade> {
    return saci.findGrades(codigo, loja)
  }

  fun updateAcerto(bean: EstoqueAcerto?) = viewModel.exec {
    bean ?: fail("Nenhum produto selecionado")
    bean.save()
    updateView()
  }
}

interface ITabEstoqueAcerto : ITabView {
  fun filtro(): FiltroAcerto
  fun updateProduto(produtos: List<EstoqueAcerto>)
  fun itensSelecionados(): List<EstoqueAcerto>
  fun filtroVazio(): FiltroProdutoEstoque
  fun autorizaAcerto(block: (user: IUser) -> Unit)
  fun produtosSelecionado(): List<ProdutoEstoqueAcerto>
}
