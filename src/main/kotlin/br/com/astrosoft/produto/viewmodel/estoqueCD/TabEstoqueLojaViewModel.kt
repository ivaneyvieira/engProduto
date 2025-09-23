package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroProdutoEstoque
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoKardec
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoque
import br.com.astrosoft.produto.model.printText.PrintProdutosEstoque
import java.time.LocalDate

class TabEstoqueLojaViewModel(val viewModel: EstoqueCDViewModel) : IModelConferencia {
  val subView
    get() = viewModel.view.tabEstoqueLoja

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoEstoque.findProdutoEstoque(filtro)
    subView.updateProduto(produtos)
  }

  fun geraPlanilha(produtos: List<ProdutoEstoque>): ByteArray {
    val planilha = PlanilhaProdutoEstoque()
    return planilha.write(produtos)
  }

  fun updateKardec() = viewModel.exec {
    val produtos: List<ProdutoEstoque> = subView.itensSelecionados()
    ProcessamentoKardec.updateKardec(produtos)
    subView.reloadGrid()
  }

  override fun updateProduto(bean: ProdutoEstoque?, updateGrid: Boolean) {
    if (bean != null) {
      bean.update()
      if (updateGrid) {
        updateView()
      }
    }
  }

  fun copiaLocalizacao() = viewModel.exec {
    val itens = subView.itensSelecionados()
    if (itens.isEmpty()) fail("Nenhum item selecionado")

    val primeiro = itens.firstOrNull() ?: fail("Nenhum item selecionado")
    itens.forEach { item ->
      item.locApp = primeiro.locApp
      item.update()
    }
    updateView()
  }

  fun imprimeProdutos() = viewModel.exec {
    val produtos = subView.itensSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    val filtro = subView.filtro()

    val report = PrintProdutosEstoque(filtro)

    report.print(
      dados = produtos, printer = subView.printerPreview(loja = 0)
    )
  }

  fun kardec(produto: ProdutoEstoque, dataIncial: LocalDate?): List<ProdutoKardec> {
    return ProcessamentoKardec.kardec(produto, dataIncial)
  }
}

interface ITabEstoqueLoja : ITabView {
  fun filtro(): FiltroProdutoEstoque
  fun updateProduto(produtos: List<ProdutoEstoque>)
  fun updateKardec()
  fun itensSelecionados(): List<ProdutoEstoque>
  fun reloadGrid()
}
