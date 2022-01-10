package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EMarcaNota
import br.com.astrosoft.produto.model.beans.FiltroNota
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.ProdutoNF

class TabNotaCDViewModel(val viewModel: NotaViewModel) {
  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaSaida.find(filtro)
    subView.updateProdutos(notas)
  }

  fun desmarcaCD() {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach{produtoNF ->
      produtoNF.marca = EMarcaNota.EXP.num
      produtoNF.salva()
    }
    updateView()
  }

  val subView
    get() = viewModel.view.tabNotaCD
}

interface ITabNotaCD : ITabView {
  fun filtro() : FiltroNota
  fun updateProdutos(notas : List<NotaSaida>)
  fun produtosSelcionados(): List<ProdutoNF>
}