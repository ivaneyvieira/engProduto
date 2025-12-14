package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.ControleKardec
import br.com.astrosoft.produto.model.beans.FiltroProdutoControle
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoControle
import br.com.astrosoft.produto.model.beans.ProdutoKardec
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

  fun updateKardec() = viewModel.exec {
    //TODO
  }

  fun imprimeProdutos() = viewModel.exec {
    //TODO
  }

  fun kardec(produto: ProdutoControle, dataIncial: LocalDate?): List<ControleKardec> {
    return produto.findKardec(dataIncial ?: LocalDate.now())
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
