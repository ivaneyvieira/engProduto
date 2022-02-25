package br.com.astrosoft.produto.viewmodel.notaEntrada

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaEntrada
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE

class TabNotaEntradaBaseViewModel(val viewModel: NotaEntradaViewModel) {
  val subView
    get() = viewModel.view.tabNotaEntradaBase

  fun updateView() {
    val filtro = subView.filtro()
    val lista = NotaEntrada.findNotaEntradaBase(filtro)
    subView.updateNotas(lista)
  }

  fun produtos(): List<ProdutoNFE> {
    return subView.notaSelecionada()?.produtosBase().orEmpty()
  }
}

interface ITabNotaEntradaBase : ITabView {
  fun updateNotas(notas: List<NotaEntrada>)
  fun notaSelecionada(): NotaEntrada?
  fun updateViewProduto()
  fun filtro(): FiltroNotaEntrada
}