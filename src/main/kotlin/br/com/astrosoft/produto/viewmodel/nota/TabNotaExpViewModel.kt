package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabNotaExpViewModel(val viewModel: NotaViewModel) {
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

  fun marcaCD() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach{produtoNF ->
      produtoNF.marca = EMarcaNota.CD.num
      produtoNF.usuario = Config.user?.login ?: ""
      produtoNF.salva()
    }
    updateView()
  }

  val subView
    get() = viewModel.view.tabNotaExp
}

interface ITabNotaExp : ITabView {
  fun filtro(): FiltroNota
  fun updateProdutos(notas: List<NotaSaida>)
  fun produtosSelcionados(): List<ProdutoNF>
}