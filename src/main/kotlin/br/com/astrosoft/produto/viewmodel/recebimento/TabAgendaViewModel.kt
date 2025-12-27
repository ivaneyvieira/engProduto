package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.produto.model.beans.Agenda
import br.com.astrosoft.produto.model.beans.FiltroAgenda
import br.com.astrosoft.framework.viewmodel.ITabView

class TabAgendaViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabAgenda

  fun updateView() {
    val filtro = subView.filtro()
    val notas = Agenda.listaAgenda(filtro)
    subView.updateNota(notas)
  }
}

interface ITabAgenda : ITabView {
  fun filtro(): FiltroAgenda
  fun updateNota(notas: List<Agenda>)
}