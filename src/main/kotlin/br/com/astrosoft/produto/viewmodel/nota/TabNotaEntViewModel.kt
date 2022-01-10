package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EMarcaNota
import br.com.astrosoft.produto.model.beans.FiltroNota
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.ProdutoNF

class TabNotaEntViewModel(val viewModel: NotaViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaNota.ENT)
    val notas = NotaSaida.find(filtro)
    subView.updateNotas(notas)
  }

  fun desmarcaEnt() {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach{produtoNF ->
      produtoNF.marca = EMarcaNota.CD.num
      produtoNF.salva()
    }
    subView.updateProdutos()
  }

  val subView
    get() = viewModel.view.tabNotaEnt
}

interface ITabNotaEnt : ITabView {
  fun filtro(marca : EMarcaNota) : FiltroNota
  fun updateNotas(notas : List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNF>
}