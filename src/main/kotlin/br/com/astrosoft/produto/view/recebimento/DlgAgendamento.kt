package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.Agenda
import br.com.astrosoft.produto.model.beans.AgendaUpdate
import br.com.astrosoft.produto.viewmodel.recebimento.TabAgendaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder
import java.time.Duration

class DlgAgendamento(val viewModel: TabAgendaViewModel) : VerticalLayout() {
  private val binder = Binder(AgendaUpdate::class.java)

  init {
    horizontalLayout {
      textField("CTe") {
        this.focus()
        bind(binder).bind(AgendaUpdate::conhecimento)
      }
      datePicker("Emissão") {
        this.localePtBr()
        this.isClearButtonVisible = true
        this.isAutoOpen = true
        bind(binder).bind(AgendaUpdate::emissaoConhecimento)
      }
    }
    horizontalLayout {
      datePicker("Data Coleta") {
        this.localePtBr()
        this.isClearButtonVisible = true
        this.isAutoOpen = true
        bind(binder).bind(AgendaUpdate::coleta)
      }
      datePicker("Data Agendada") {
        this.localePtBr()
        this.isClearButtonVisible = true
        this.isAutoOpen = true
        bind(binder).bind(AgendaUpdate::data)
      }
    }
    horizontalLayout {
      timePicker("Horário") {
        this.step = Duration.ofMinutes(30)
        bind(binder).bind(AgendaUpdate::hora)
      }
      integerField("Recebedor") {
        bind(binder).bind(AgendaUpdate::recebedor)
      }
    }
  }

  fun edtAgendamento(agenda: Agenda) {
    val form =
        SubWindowForm(title = "Nr. Ordem ${agenda.invno}  NF ${agenda.nf}", toolBar = ::toolBar, fullSize = false) {
          binder.bean = agenda.agendaUpdate()
          this
        }
    form.open()
  }

  private fun toolBar(hasComponents: HasComponents, subWindowForm: SubWindowForm) {
    hasComponents.apply {
      this.button("Salvar") {
        this.addThemeVariants(LUMO_PRIMARY)
        onClick {
          viewModel.salvaAgendamento(binder.bean)
          subWindowForm.close()
        }
      }
    }
  }
}