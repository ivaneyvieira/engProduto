package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroProduto
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoRetiraEntrega

class TabProdutoRetiraEntregaViewModel(val viewModel: ProdutoViewModel) {
  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoRetiraEntrega.find(filtro)
    subView.updateProdutos(produtos)
  }

  fun findGrade(prd: ProdutoRetiraEntrega?, block: (List<PrdGrade>) -> Unit) = viewModel.exec {
    prd ?: fail("Produto não encontrado")
    val list = prd.findGrades()
    block(list)
  }

  fun salvaGrade(prd: ProdutoRetiraEntrega?, grade: String?)= viewModel.exec {
    prd ?: fail("Produto não selecionado")
    prd.gradeAlternativa = grade ?: ""
    prd.salvaGrade()
  }

  val subView
    get() = viewModel.view.tabProdutoRetiraEntrega
}

interface ITabProdutoRetiraEntrega : ITabView {
  fun filtro(): FiltroProduto
  fun updateProdutos(produtos: List<ProdutoRetiraEntrega>)
}