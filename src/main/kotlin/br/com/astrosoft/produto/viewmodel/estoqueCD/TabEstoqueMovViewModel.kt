package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.block
import br.com.astrosoft.framework.viewmodel.update
import br.com.astrosoft.produto.model.beans.FiltroProdutoEstoque
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoque
import com.github.javaparser.printer.concretesyntaxmodel.CsmElement.block

class TabEstoqueMovViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueMov

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    viewModel.execAsync {
      block {
        val filtro = subView.filtro()
        val produtos = ProdutoEstoque.findProdutoEstoque(filtro)
        produtos
      }
      update {
        subView.updateProduto(it)
      }
    }
  }

  fun geraPlanilha(produtos: List<ProdutoEstoque>): ByteArray {
    val planilha = PlanilhaProdutoEstoque()
    return planilha.write(produtos)
  }
}

interface ITabEstoqueMov : ITabView {
  fun filtro(): FiltroProdutoEstoque
  fun updateProduto(produtos: List<ProdutoEstoque>)
}
