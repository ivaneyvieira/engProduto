package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintProdutosConferenciaEstoque
import br.com.astrosoft.produto.model.saci

class TabEstoqueGarantiaViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueGarantia

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoEstoqueGarantia.findAll(filtro).agrupaGarantia().sortedBy { it.numero }
    subView.updateProduto(produtos)
  }

  fun imprimirPedido(garantia: EstoqueGarantia) = viewModel.exec {
    val produtos = garantia.findProdutos()

    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val report = PrintProdutosConferenciaEstoque("Pedido de garantia: ${garantia.numero}")

    report.print(
      dados = produtos, printer = subView.printerPreview()
    )
  }

  fun imprimirGarantia(garantia: EstoqueGarantia) = viewModel.exec {
    val produtos = garantia.findProdutos().filter {
      (it.diferenca ?: 0) != 0
    }.sortedBy { it.diferenca ?: 999999 }
    if (produtos.isEmpty()) {
      fail("Nenhum produto válido selecionado")
    }

    val report = PrintProdutosConferenciaGarantia()

    report.print(
      dados = produtos, printer = subView.printerPreview()
    )
  }

  fun cancelarGarantia() = viewModel.exec {
    val itensSelecionado = subView.itensSelecionados().filter {
      it.processado == false
    }
    if (itensSelecionado.isEmpty()) {
      fail("Nenhum garantia não processado selecionado")
    }
    viewModel.view.showQuestion("Confirma o cancelamento do garantia selecionado?") {
      itensSelecionado.forEach {
        it.cancela()
      }
      updateView()
    }
  }

  fun imprimirRelatorio(garantia: EstoqueGarantia) {
    val produtos = garantia.findProdutos()
    val report = ReportGarantia()
    val file = report.processaRelatorio(produtos)
    viewModel.view.showReport(chave = "Garantia${System.nanoTime()}", report = file)
  }

  fun geraPlanilha(produtos: List<ProdutoEstoqueGarantia>): ByteArray {
    val planilha = PlanilhaProdutoEstoqueGarantia()
    return planilha.write(produtos)
  }

  fun updateProduto(produto: ProdutoEstoqueGarantia) = viewModel.exec {
    produto.save()
  }

  fun gravaGarantia(garantia: EstoqueGarantia) = viewModel.exec {
    if (garantia.gravado == true) {
      fail("Garantia já gravado")
    }
    subView.autorizaGarantia { user ->
      val pordutos = garantia.findProdutos()
      pordutos.forEach {
        it.gravadoLogin = user.no
        it.gravado = true
        it.save()
      }
      updateView()
    }
  }

  fun removeGarantia() = viewModel.exec {
    val itensSelecionado = subView.produtosSelecionado()

    itensSelecionado.ifEmpty {
      fail("Nenhum garantia selecionado")
    }

    if (itensSelecionado.any { it.processado == true }) {
      fail("Garantia está processado")
    }

    subView.autorizaGarantia {
      itensSelecionado.forEach { produto ->
        produto.remove()
      }
      updateView()
    }
  }

  fun addProduto(produto: ProdutoEstoqueGarantia) {
    produto.save()
    updateView()
  }

  fun findProdutos(codigo: String, loja: Int): List<PrdGrade> {
    return saci.findGrades(codigo, loja)
  }

  fun updateGarantia(bean: EstoqueGarantia?) = viewModel.exec {
    bean ?: fail("Nenhum produto selecionado")
    bean.save()
    updateView()
  }
}

interface ITabEstoqueGarantia : ITabView {
  fun filtro(): FiltroGarantia
  fun updateProduto(produtos: List<EstoqueGarantia>)
  fun itensSelecionados(): List<EstoqueGarantia>
  fun filtroVazio(): FiltroProdutoEstoque
  fun autorizaGarantia(block: (user: IUser) -> Unit)
  fun produtosSelecionado(): List<ProdutoEstoqueGarantia>
}
