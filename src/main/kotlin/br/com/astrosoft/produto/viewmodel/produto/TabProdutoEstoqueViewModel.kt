package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoSaldoLoja
import br.com.astrosoft.produto.model.printText.PrintProdutosLoja

class TabProdutoEstoqueViewModel(val viewModel: ProdutoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    subView.execThread {
      val filtro = subView.filtro()
      val produtos = ProdutoLoja.findProdutoSaldo(filtro)

      subView.updateProdutos(produtos)
    }
  }

  fun geraPlanilha(produtos: List<ProdutoLoja>): ByteArray {
    val planilha = PlanilhaProdutoSaldoLoja()
    return planilha.write(produtos)
  }

  fun imprimeProdutos() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    val filtro = subView.filtro()

    val report = PrintProdutosLoja(filtro)

    report.print(
      dados = produtos,
      printer = subView.printerPreview(loja = 0)
    )
  }

  val subView
    get() = viewModel.view.tabProdutoEstoque
}

interface ITabProdutoEstoque : ITabView {
  fun filtro(): FiltroProdutoLoja
  fun updateProdutos(produtos: List<ProdutoLoja>)
  fun produtosSelecionados(): List<ProdutoLoja>
  fun openValidade(tipoValidade: Int, tempoValidade: Int, block: (ValidadeSaci) -> Unit)
}