package br.com.astrosoft.produto.viewmodel.notaEntrada

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaEntrada
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE

class TabNotaEntradaRecebidoViewModel(val viewModel: NotaEntradaViewModel) {
  val subView
    get() = viewModel.view.tabNotaEntradaRecebido

  fun updateView() {
    val lista = NotaEntrada.findNotaEntradaRecebido()
    subView.updateNotas(lista)
  }

  fun produtos(): List<ProdutoNFE> {
    return subView.notaSelecionada()?.produtosRecebido().orEmpty()
  }
}

interface ITabNotaEntradaRecebido : ITabView {
  fun updateNotas(notas: List<NotaEntrada>)
  fun notaSelecionada(): NotaEntrada?
  fun updateViewProduto()
}