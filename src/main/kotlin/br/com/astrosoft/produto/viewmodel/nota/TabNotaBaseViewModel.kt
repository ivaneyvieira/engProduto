package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabNotaBaseViewModel(val viewModel: NotaViewModel) {
  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaSaida.find(filtro)
    subView.updateProdutos(notas)
  }

  fun findGrade(prd: ProdutoNF?, block: (List<PrdGrade>) -> Unit) = viewModel.exec {
    prd ?: return@exec
    val list = prd.findGrades()
    block(list)
  }

  fun marcaEntrega() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach{produtoNF ->
      produtoNF.marca = EMarcaNota.ENTREGA.num
      produtoNF.salva()
    }
    updateView()
  }

  val subView
    get() = viewModel.view.tabNotaBase
}

interface ITabNotaBase : ITabView {
  fun filtro(): FiltroNota
  fun updateProdutos(notas: List<NotaSaida>)
  fun produtosSelcionados(): List<ProdutoNF>
}