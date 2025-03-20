package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoqueAcerto
import br.com.astrosoft.produto.model.printText.PrintProdutosConferenciaAcerto
import br.com.astrosoft.produto.model.report.ReportAcerto

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

  fun imprimir(acerto: EstoqueAcerto) = viewModel.exec {
    val produtos = acerto.findProdutos().filter {
      (it.diferenca ?: 0) != 0
    }.sortedBy { it.diferenca ?: 999999 }
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
      it.processado == "Não"
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

  fun updateProduto(produto: ProdutoEstoqueAcerto) {
    produto.save()
  }
}

interface ITabEstoqueAcerto : ITabView {
  fun filtro(): FiltroAcerto
  fun updateProduto(produtos: List<EstoqueAcerto>)
  fun itensSelecionados(): List<EstoqueAcerto>
}
