package br.com.astrosoft.framework.view.vaadin.helper

import com.flowingcode.vaadin.addons.gridhelpers.GridHelper
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.ListDataProvider
import java.util.*

fun Component.style(name: String, value: String) {
  this.element.style.set(name, value)
}

fun DatePicker.localePtBr() {
  this.locale = Locale.Builder().setLanguage("pt").setRegion("BR").build()
  this.i18n =
      DatePicker.DatePickerI18n().apply {
        this.setDateFormat("dd/MM/yyyy")
        this.today = "hoje"
        this.cancel = "cancelar"
        this.firstDayOfWeek = 0
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

fun Grid<*>.layoutConfig() {
  this.addThemeName(GridHelper.DENSE_THEME)
  addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES)
}

@VaadinDsl
fun (@VaadinDsl HasComponents).verticalBlock(
  label: String? = null,
  block: (@VaadinDsl VerticalLayout).() -> Unit = {}
): VerticalLayout {
  val layout: VerticalLayout = VerticalLayout().apply {
    this.isPadding = false
    this.isSpacing = false
    this.isMargin = false
    this.isExpand = true
    content { align(left, top) }
    if (label != null) {
      h5(label)
      hr()
    }
  }
  return init(layout, block)
}

@VaadinDsl
fun (@VaadinDsl HasComponents).horizontalBlock(
  block: (@VaadinDsl HorizontalLayout).() -> Unit = {}
): HorizontalLayout {
  val layout: HorizontalLayout = HorizontalLayout().apply {
    this.isPadding = false
    this.isSpacing = false
    this.isMargin = false
    this.isExpand = true
    content { align(left, top) }
  }
  return init(layout, block)
}