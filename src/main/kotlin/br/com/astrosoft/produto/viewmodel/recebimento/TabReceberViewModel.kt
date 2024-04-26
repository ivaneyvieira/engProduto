package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaRecebimentoProduto
import br.com.astrosoft.produto.model.beans.NotaRecebimento

class TabReceberViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabReceber

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimento.findAll(filtro)
    subView.updateNota(notas)
  }
}

interface ITabReceber : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
}