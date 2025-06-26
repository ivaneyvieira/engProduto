package br.com.astrosoft.framework.view.vaadin.helper

import br.com.astrosoft.framework.util.DATETIME_PATTERN
import br.com.astrosoft.framework.util.DATE_PATTERN
import br.com.astrosoft.framework.util.TIME_PATTERN
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.Column
import com.vaadin.flow.component.grid.HeaderRow
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.function.ValueProvider
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KProperty1

class ColumnGroup<T : Any>(val grid: Grid<T>, private val header: String) {
  private val columns: MutableList<Column<T>> = mutableListOf()
  private var headerRow: HeaderRow? = null

  fun addColumn(column: Column<T>): Column<T> {
    headerRow = if (grid.headerRows.size == 1) {
      grid.prependHeaderRow()
    } else {
      grid.headerRows[0]
    }
    columns.add(column)
    return column
  }

  fun join() {
    headerRow?.join(* columns.toTypedArray())?.component = Div(header).apply {
      this.style["text-align"] = "center"
      this.style["width"] = "100%"
      this.style["margin"] = "0"
      this.style["padding"] = "0"
      this.style["font-size"] = "1em"
      this.style["font-weight"] = "bold"
    }
  }
}

fun <T : Any> Grid<T>.columnGroup(header: String, block: ColumnGroup<T>.() -> Unit) {
  val columnGroup = ColumnGroup(this, header)
  columnGroup.block()
  columnGroup.join()
}

fun <T : Any> (@VaadinDsl ColumnGroup<T>).addColumnButton(
  iconButton: VaadinIcon,
  tooltip: String? = null,
  header: String? = null,
  configIcon: (Icon, T) -> Unit = { _, _ -> },
  execButton: (T) -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.addColumnButton(iconButton, tooltip, header, configIcon, execButton))
}

fun <T : Any> (@VaadinDsl ColumnGroup<T>).addColumnSeq(label: String, width: String? = null): Column<T> {
  return this.addColumn(this.grid.addColumnSeq(label, width))
}

@JvmName("columnProviderString")
fun <T : Any, V : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  valueProvider: ValueProvider<T, V?>,
  key: String? = null,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(valueProvider, key, header, width, isExpand, block))
}

@JvmName("columnGroupString")
fun <T : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  property: KProperty1<T, String?>,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(property, header, width, isExpand, block))
}

@JvmName("columnGroupSet")
fun <T : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  property: KProperty1<T, Set<Any>>,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(property, header, width, isExpand, block))
}

@JvmName("columnGroupBoolean")
fun <T : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  property: KProperty1<T, Boolean?>,
  header: String? = null,
  width: String? = null,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(property, header, width, block))
}

@JvmName("columnGroupLocalDate")
fun <T : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  property: KProperty1<T, LocalDate?>,
  header: String? = null,
  width: String? = "120px",
  isExpand: Boolean = false,
  pattern: String = DATE_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(property, header, width, isExpand, pattern, block))
}

@JvmName("columnGroupDate")
fun <T : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  property: KProperty1<T, Date?>,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  pattern: String = DATE_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(property, header, width, isExpand, pattern, block))
}

@JvmName("columnGroupLocalTime")
fun <T : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  property: KProperty1<T, LocalTime?>,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  pattern: String = TIME_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(property, header, width, isExpand, pattern, block))
}

@JvmName("columnGroupTime")
fun <T : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  property: KProperty1<T, Time?>,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  pattern: String = TIME_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(property, header, width, isExpand, pattern, block))
}

@JvmName("columnGroupLocalDateTime")
fun <T : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  property: KProperty1<T, LocalDateTime?>,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  pattern: String = DATETIME_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(property, header, width, isExpand, pattern, block))
}

@JvmName("columnGroupDouble")
fun <T : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  property: KProperty1<T, Double?>,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  pattern: String = "#,##0.00",
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(property, header, width, isExpand, pattern, block))
}

@JvmName("columnGroupInt")
fun <T : Any> (@VaadinDsl ColumnGroup<T>).columnGrid(
  property: KProperty1<T, Int?>,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  pattern: String = "0",
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumn(this.grid.columnGrid(property, header, width, isExpand, pattern, block))
}