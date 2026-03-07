package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroProdutoSaldoEstoque
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoSaldoEstoque
import br.com.astrosoft.produto.model.beans.ValidadeSaci
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoSaldoEstoque

class TabSaldoEstoqueViewModel(val viewModel: ProdutoViewModel) {
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
      val produtos = ProdutoSaldoEstoque.findProdutoSaldoEstoque(filtro)

      subView.updateProdutos(produtos)
    }
  }

  fun geraPlanilha(produtos: List<ProdutoSaldoEstoque>): ByteArray {
    val planilha = PlanilhaProdutoSaldoEstoque()
    return planilha.write(produtos)
  }

  fun imprimeProdutos() = viewModel.exec {
    /*
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    val filtro = subView.filtro()

    val report = PrintProdutos(filtro)

    report.print(
      dados = produtos,
      printer = subView.printerPreview(loja = 0)
    )
     */
  }

  val subView
    get() = viewModel.view.tabSaldoEstoque
}

interface ITabSaldoEstoque : ITabView {
  fun filtro(): FiltroProdutoSaldoEstoque
  fun updateProdutos(produtos: List<ProdutoSaldoEstoque>)
  fun produtosSelecionados(): List<ProdutoSaldoEstoque>
  fun openValidade(tipoValidade: Int, tempoValidade: Int, block: (ValidadeSaci) -> Unit)
}