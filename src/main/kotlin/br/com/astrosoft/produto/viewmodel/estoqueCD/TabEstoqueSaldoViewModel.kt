package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.block
import br.com.astrosoft.framework.viewmodel.update
import br.com.astrosoft.produto.model.beans.FiltroProdutoEstoque
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoKardec
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoque
import java.time.LocalDate

class TabEstoqueSaldoViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueSaldo

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()

    viewModel.execAsync {
      block {
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

  fun kardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    val dataInicial = produto.dataInicial ?: LocalDate.now().withDayOfMonth(1)
    val lista: List<ProdutoKardec> =
        produto.recebimentos(dataInicial) + produto.ressuprimento(dataInicial) + produto.expedicao(dataInicial) + produto.reposicao(
          dataInicial
        ) + produto.saldoAnterior(dataInicial) + produto.acertoEstoque(dataInicial)
    var saldoAcumulado = 0
    return lista.sortedWith(compareBy({ it.data }, { it.loja }, { it.doc })).map {
      saldoAcumulado += it.qtde
      it.copy(saldo = saldoAcumulado)
    }
  }

  fun updateProduto(bean: ProdutoEstoque?) {
    bean?.update()
  }
}

interface ITabEstoqueSaldo : ITabView {
  fun filtro(): FiltroProdutoEstoque
  fun updateProduto(produtos: List<ProdutoEstoque>)
}
