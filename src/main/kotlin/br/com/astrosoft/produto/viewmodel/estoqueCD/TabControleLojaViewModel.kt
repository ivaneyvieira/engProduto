package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.ControleKardec
import br.com.astrosoft.produto.model.beans.FiltroProdutoControle
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoControle
import java.time.LocalDate

class TabControleLojaViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabControleLoja

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoControle.findProdutoControle(filtro)
    subView.updateProduto(produtos)
  }

  fun updateKardec(dataIncial : LocalDate?) = viewModel.exec {
    val listProdutos = subView.itensSelecionados()
    if (listProdutos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    listProdutos.forEach {
      kardec(produto = it, dataIncial = dataIncial)
    }

    updateView()
  }

  fun imprimeProdutos() = viewModel.exec {
    //TODO
  }

  fun kardec(produto: ProdutoControle, dataIncial : LocalDate?): List<ControleKardec> {
    if(produto.dataInicial == null){
      produto.dataInicial = dataIncial ?: LocalDate.now()
    }
    val listaKardex = produto.findKardec()
    val saldoKardex = listaKardex.lastOrNull()?.saldo ?: 0
    produto.kardexLoja = saldoKardex
    produto.updateControle()
    return listaKardex
  }

  fun updateControle(produto: ProdutoControle) {
    produto.updateControle()
  }
}

interface ITabControleLoja : ITabView {
  fun filtro(): FiltroProdutoControle
  fun updateProduto(produtos: List<ProdutoControle>)
  fun updateKardec()
  fun itensSelecionados(): List<ProdutoControle>
  fun reloadGrid()
}
