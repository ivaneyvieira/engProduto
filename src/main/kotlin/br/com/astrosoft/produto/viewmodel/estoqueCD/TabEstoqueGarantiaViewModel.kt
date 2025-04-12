package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoqueGarantia
import br.com.astrosoft.produto.model.printText.PrintProdutosConferenciaGarantia
import br.com.astrosoft.produto.model.report.ReportGarantia
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

    val report = PrintProdutosConferenciaGarantia("Pedido de garantia: ${garantia.numero}")

    report.print(
      dados = produtos, printer = subView.printerPreview()
    )
  }

  fun cancelarGarantia() = viewModel.exec {
    val itensSelecionado = subView.itensSelecionados()
    if (itensSelecionado.isEmpty()) {
      fail("Nenhum garantia não processado selecionado")
    }
    viewModel.view.showQuestion("Confirma o cancelamento do garantia selecionado?") {
      itensSelecionado.forEach {
        it.cancelaGarantia()
      }
      updateView()
    }
  }

  fun geraPlanilha(produtos: List<ProdutoEstoqueGarantia>): ByteArray {
    val planilha = PlanilhaProdutoEstoqueGarantia()
    return planilha.write(produtos)
  }

  fun updateProduto(produto: ProdutoEstoqueGarantia) = viewModel.exec {
    produto.saveGarantia()
  }

  fun removeGarantia() = viewModel.exec {
    val itensSelecionado = subView.produtosSelecionado()

    itensSelecionado.ifEmpty {
      fail("Nenhum garantia selecionado")
    }

    subView.autorizaGarantia {
      itensSelecionado.forEach { produto ->
        produto.remove()
      }
      updateView()
    }
  }

  fun addProduto(produto: ProdutoEstoqueGarantia) {
    produto.saveGarantia()
    updateView()
  }

  fun findProdutos(codigo: String, loja: Int): List<PrdGrade> {
    return saci.findGrades(codigo, loja)
  }

  fun updateGarantia(bean: EstoqueGarantia?) = viewModel.exec {
    bean ?: fail("Nenhum produto selecionado")
    bean.saveGarantia()
    updateView()
  }

  fun imprimirRelatorio(garantia: EstoqueGarantia) {
    val produtos = garantia.findProdutos()
    val report = ReportGarantia()
    val file = report.processaRelatorio(produtos)
    viewModel.view.showReport(chave = "Acerto${System.nanoTime()}", report = file)
  }

  fun copiaEstoque() = viewModel.exec {
    val itensSelecionado = subView.produtosSelecionado()
    if (itensSelecionado.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    subView.formSeleionaEstoque { tipo ->
      itensSelecionado.forEach {
        if (tipo == null) {
          fail("Selecione o tipo de estoque")
        } else {
          it.estoqueDev = when (tipo) {
            TipoEstoque.LOJA  -> {
              it.estoqueLoja
            }

            TipoEstoque.LOJAS -> {
              it.estoqueLojas
            }
          }

          it.saveGarantia()
          subView.updateProduto()
        }
      }
    }
  }
}

interface ITabEstoqueGarantia : ITabView {
  fun filtro(): FiltroGarantia
  fun updateProduto(produtos: List<EstoqueGarantia>)
  fun updateProduto()
  fun itensSelecionados(): List<EstoqueGarantia>
  fun filtroVazio(): FiltroProdutoEstoque
  fun autorizaGarantia(block: (user: IUser) -> Unit)
  fun produtosSelecionado(): List<ProdutoEstoqueGarantia>
  fun formSeleionaEstoque(block: (estoque: TipoEstoque?) -> Unit)
}

enum class TipoEstoque(val descricao: String) {
  LOJA("Loja"),
  LOJAS("Lojas"),
}