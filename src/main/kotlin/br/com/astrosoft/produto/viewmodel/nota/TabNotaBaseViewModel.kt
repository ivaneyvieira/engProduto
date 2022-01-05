package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNota
import br.com.astrosoft.produto.model.beans.NotaSaida

class TabNotaBaseViewModel(val viewModel: NotaViewModel) {
  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaSaida.find(filtro)
    subView.updateProdutos(notas)
  }

  val subView
    get() = viewModel.view.tabNotaBase
}

interface ITabNotaBase : ITabView {
  fun filtro() : FiltroNota
  fun updateProdutos(notas : List<NotaSaida>)
}