package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*
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
    val produtos = ProdutoEstoque.findProdutoEstoque(filtro)
    subView.updateProduto(produtos)
  }

  fun geraPlanilha(produtos: List<ProdutoEstoque>): ByteArray {
    val planilha = PlanilhaProdutoEstoque()
    return planilha.write(produtos)
  }

  fun kardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    //val userAdmin = UserSaci.userAdmin()
    val dataInicial = produto.dataInicial ?: LocalDate.now().withDayOfMonth(1)
    val lista: List<ProdutoKardec> = produto.recebimentos(dataInicial) + produto.ressuprimento(dataInicial) +
                                     produto.expedicao(dataInicial) + produto.reposicao(dataInicial) +
                                     produto.saldoAnterior(dataInicial)
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
