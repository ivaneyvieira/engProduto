package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.Agenda
import br.com.astrosoft.produto.model.beans.AgendaUpdate
import br.com.astrosoft.produto.model.beans.FiltroAgenda
import java.time.LocalDate
import java.time.LocalTime

class TabPreEntradaViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabPreEntrada

  fun updateView() {
    val filtro = subView.filtro()
    val notas = Agenda.listaAgenda(filtro).filter {
      it.tipoAgenda == filtro.tipoAgenda
    }
    subView.updateNota(notas)
  }

  fun salvaAgendamento(bean: AgendaUpdate?) = viewModel.exec {
    bean ?: fail("Agendamento inválido")
    val newbean =
        if (bean.dataRecbedor == null && (bean.recebedor ?: 0) > 0) bean.copy(
          dataRecbedor = LocalDate.now(),
          horaRecebedor = LocalTime.now()
        )
        else bean
    newbean.save()
    updateView()
  }
}

interface ITabPreEntrada : ITabView {
  fun filtro(): FiltroAgenda
  fun updateNota(notas: List<Agenda>)
}