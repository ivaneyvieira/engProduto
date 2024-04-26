package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaRecebimentoProduto
import br.com.astrosoft.produto.model.beans.NotaRecebimento

class TabRecebidoViewModel(val viewModel: RecebimentoViewModel) {
    val subView
    get() = viewModel.view.tabRecebido

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimento.findAll(filtro)
    subView.updateNota(notas)
  }
}

interface ITabRecebido : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
}