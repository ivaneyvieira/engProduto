package br.com.astrosoft.framework.view.vaadin.helper

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.data.provider.ListDataProvider
import java.util.*

fun Component.style(name: String, value: String) {
  this.element.style.set(name, value)
}

fun DatePicker.localePtBr() {
  this.locale = Locale("pt-br")
  this.i18n =
      DatePicker.DatePickerI18n().apply {
        this.setDateFormat("dd/MM/yyyy")
        this.today = "hoje"
        this.cancel = "cancelar"
        this.firstDayOfWeek = 1
        this.monthNames = listOf(
          "janeiro",
          "fevereiro",
          "março",
          "abril",
          "maio",
          "junho",
          "julho",
          "agosto",
          "setembro",
          "outubro",
          "novembro",
          "dezembro"
        )
        this.weekdays = listOf("domingo", "segunda", "terça", "quarta", "quinta", "sexta", "sábado")
        this.weekdaysShort = listOf("dom", "seg", "ter", "qua", "qui", "sex", "sab")
      }
}

fun <T> ListDataProvider<T>.updateItens(itens: List<T>) {
  this.items.clear() //  this.items.addAll(itens.sortedBy {it.hashCode()})
  this.items.addAll(itens)
  this.refreshAll()
}