package br.com.astrosoft.produto.viewmodel.notaEntrada

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaEntrada
import br.com.astrosoft.produto.model.beans.NotaEntrada

class TabNotaEntradaPendenteViewModel(val viewModel: NotaEntradaViewModel) {
  val subView
    get() = viewModel.view.tabNotaEntradaPendente

  fun updateView() {
    val lista = NotaEntrada.findNotaEntradaPendente(FiltroNotaEntrada())
    subView.updateNotas(lista)
  }

  fun marcaProdutos(barCode: String?, quant: Int?) {
    val nota = subView.notaSelecionada()
    nota?.addProdutoConf(barCode ?: "", quant ?: 0)
    subView.updateViewProduto()
  }
}

interface ITabNotaEntradaPendente : ITabView {
  fun updateNotas(notas: List<NotaEntrada>)
  fun notaSelecionada(): NotaEntrada?
  fun updateViewProduto()
}