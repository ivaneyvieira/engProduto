package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroProdutoSaldoAtacado
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoSaldoAtacado
import br.com.astrosoft.produto.model.beans.ValidadeSaci

class TabAtacadoViewModel(val viewModel: ProdutoViewModel) {
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
      val produtos = ProdutoSaldoAtacado.findProdutoSaldo(filtro).filter { nota ->
        (nota.estoqueLojasAtacado ?: 0) != 0
      }

      subView.updateProdutos(produtos)
    }
  }

  fun geraPlanilha(produtos: List<ProdutoSaldoAtacado>): ByteArray {
    //val planilha = PlanilhaProdutoSaldo()
    //return planilha.write(produtos)
    TODO()
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
    get() = viewModel.view.tabAtacado
}

interface ITabAtacado : ITabView {
  fun filtro(): FiltroProdutoSaldoAtacado
  fun updateProdutos(produtos: List<ProdutoSaldoAtacado>)
  fun produtosSelecionados(): List<ProdutoSaldoAtacado>
  fun openValidade(tipoValidade: Int, tempoValidade: Int, block: (ValidadeSaci) -> Unit)
}