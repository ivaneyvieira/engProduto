package br.com.astrosoft.framework.view.vaadin.helper

import br.com.astrosoft.framework.util.format
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.*
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.miki.superfields.numbers.SuperDoubleField
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

//***********************************************************************************************
//Editores de colunas
//***********************************************************************************************

fun <T : Any> Grid<T>.withEditor(
  classBean: KClass<T>,
  isBuffered : Boolean = false,
  openEditor: (Binder<T>) -> Unit,
  closeEditor: (Binder<T>) -> Unit,
  saveEditor: (Binder<T>) -> Unit = { _ -> },
  canEdit: (T?) -> Boolean = { true },
) {
  val binder = Binder(classBean.java)
  editor.binder = binder
  editor.isBuffered = isBuffered
  addItemDoubleClickListener { event ->
    val bean = this.selectedItems.firstOrNull()
    if (canEdit(bean)) {
      editor.editItem(event.item)
    }
  }
  editor.addOpenListener {
    openEditor(binder)
  }
  editor.addCloseListener {
    closeEditor(binder)
  }
  editor.addSaveListener {
    saveEditor(it.grid.editor.binder)
  }
}

fun <T : Any> Grid<T>.focusEditor(property: KProperty1<T, *>) {
  if (this.editor.isOpen) {
    val component = this.getColumnBy(property).editorComponent as? Focusable<*>
    component?.focus()
  }
}

fun <T : Any> Grid<T>.setReadOnly(property: KProperty1<T, *>) {
  if (this.editor.isOpen) {
    val component = this.getColumnBy(property).editorComponent as? HasValue<*, *>
    component?.isReadOnly = true
  }
}

fun <T : Any> Grid<T>.editorComponent(property: KProperty1<T, *>): Component? =
    getColumnBy(property).editorComponent

fun <T : Any> Grid<T>.focus(property: KProperty1<T, *>) {
  (editorComponent(property) as? Focusable<*>)?.focus()
}

fun <T : Any> Grid.Column<T>.decimalFieldEditor(): Grid.Column<T> {
  val component = decimalFieldComponent()
  component.element.addEventListener("keydown") { _ ->
    grid.editor.cancel()
  }.filter = "event.key === 'Enter'"
  grid.editor.binder.forField(component).bind(this.key)
  this.editorComponent = component
  return this
}

fun <T : Any> Grid.Column<T>.textAreaEditor(block: TextArea.() -> Unit = {}): Grid.Column<T> {
  val component = textAreaComponente()
  component.block()
  grid.editor.binder.forField(component).bind(this.key)
  this.editorComponent = component
  return this
}

fun <T : Any> Grid.Column<T>.integerFieldEditor(block: IntegerField.() -> Unit = {}): Grid.Column<T> {
  val component = integerFieldComponente()
  component.element.addEventListener("keydown") { _ ->
    grid.editor.save()
    grid.editor.closeEditor()
  }.filter = "event.key === 'Enter'"
  component.element.addEventListener("keydown") { _ ->
    grid.editor.cancel()
    grid.editor.closeEditor()
  }.filter = "event.key === 'Escape' || event.key === 'Esc'"
  component.block()
  grid.editor.binder.forField(component).bind(this.key)
  this.editorComponent = component
  return this
}

fun <T : Any> Grid.Column<T>.textFieldEditor(block: TextField.() -> Unit = {}): Grid.Column<T> {
  val grid = this.grid
  val component = textFieldComponente()
  component.block()
  component.element.addEventListener("keydown") { _ ->
    grid.editor.cancel()
  }.filter = "event.key === 'Enter'"
  grid.editor.binder.forField(component).bind(this.key)
  this.editorComponent = component
  return this
}

fun <T : Any> Grid.Column<T>.dateFieldEditor(block: (DatePicker) -> Unit = {}): Grid.Column<T> {
  val grid = this.grid
  val component = dateFieldComponente()
  block(component)
  component.element.addEventListener("keydown") { _ ->
    grid.editor.cancel()
  }.filter = "event.key === 'Enter'"
  grid.editor.binder.forField(component).bind(this.key)
  this.editorComponent = component
  return this
}

fun <T : Any> Grid.Column<T>.mesAnoFieldEditor(): Grid.Column<T> {
  val grid = this.grid
  val component = mesAnoFieldComponente()
  component.element.addEventListener("keydown") { _ ->
    grid.editor.cancel()
  }.filter = "event.key === 'Enter'"
  component.setOverlayWidth("100px")
  grid.editor.binder.forField(component).bind(this.key)
  this.editorComponent = component

  return this
}

fun <T : Any, V : Any?> Grid.Column<T>.comboFieldEditor(block: (Select<V>) -> Unit = {}): Grid.Column<T> {
  val grid = this.grid
  val component = Select<V>().apply {
    block(this)
    this.isEmptySelectionAllowed = true
    this.setWidthFull()
  }
  component.element.addEventListener("keydown") {
    grid.editor.cancel()
  }.filter = "event.key === 'Enter'"
  grid.editor.binder.forField(component).bind(this.key)
  this.editorComponent = component
  return this
}

//***********************************************************************************************

private fun decimalFieldComponent(): SuperDoubleField {
  return SuperDoubleField().apply {
    this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
    this.setSizeFull()
    addThemeVariants(TextFieldVariant.LUMO_SMALL)
    this.valueChangeMode = ValueChangeMode.ON_CHANGE
    this.isAutoselect = true
    this.locale = Locale.forLanguageTag("pt-BR")
  }
}

private fun textFieldComponente() = TextField().apply {
  this.valueChangeMode = ValueChangeMode.ON_CHANGE
  addThemeVariants(TextFieldVariant.LUMO_SMALL)
  this.isAutoselect = true
  this.isClearButtonVisible = true
  this.setWidthFull()
  setSizeFull()
}

private fun dateFieldComponente() = DatePicker().apply {
  this.localePtBr()
  this.isClearButtonVisible = true
  this.setWidthFull()
  setSizeFull()
}

fun mesAnoFieldComponente() = ComboBox<String>().apply {
  this.isClearButtonVisible = true
  this.setWidthFull()
  this.setSizeFull()
  this.isAllowCustomValue = true
  val dateListDepois = (0..12 * 15).map { num ->
    LocalDate.now().withDayOfMonth(15).plusMonths(num.toLong())
  }
  val dateListAntes = (1..6).map { num ->
    LocalDate.now().withDayOfMonth(15).minusMonths(num.toLong())
  }
  val dateList = dateListAntes + dateListDepois
  this.setItems(dateList.sorted().map { it.format("MM/yy") }.distinct())
}

private fun integerFieldComponente() = IntegerField().apply {
  this.valueChangeMode = ValueChangeMode.ON_CHANGE
  addThemeVariants(TextFieldVariant.LUMO_SMALL)
  this.isAutoselect = true
  setSizeFull()
}

private fun textAreaComponente() = TextArea().apply {
  this.valueChangeMode = ValueChangeMode.ON_CHANGE
  style.set("maxHeight", "50em")
  style.set("minHeight", "2em")
  addThemeVariants(TextAreaVariant.LUMO_SMALL)
  this.isAutoselect = true
  setSizeFull()
}

class BigDecimalToDoubleConverter : Converter<BigDecimal, Double> {
  override fun convertToPresentation(value: Double?, context: ValueContext?): BigDecimal {
    value ?: return BigDecimal.valueOf(0.00)
    return BigDecimal.valueOf(value)
  }

  override fun convertToModel(value: BigDecimal?, context: ValueContext?): Result<Double> {
    return Result.ok(value?.toDouble() ?: 0.00)
  }
}