package br.com.astrosoft.framework.view.vaadin.helper

import br.com.astrosoft.framework.util.DATETIME_PATTERN
import br.com.astrosoft.framework.util.DATE_PATTERN
import br.com.astrosoft.framework.util.TIME_PATTERN
import br.com.astrosoft.framework.util.format
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.kaributools.addColumnFor
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.Column
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.data.renderer.LocalDateRenderer
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer
import com.vaadin.flow.data.renderer.NumberRenderer
import com.vaadin.flow.data.renderer.TextRenderer
import java.sql.Time
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KProperty1

fun <T : Any> (@VaadinDsl Grid<T>).addColumnButton(
  iconButton: VaadinIcon,
  tooltip: String? = null,
  execButton: (T) -> Unit = {},
  configIcon: (Icon, T) -> Unit = { _, _ -> },
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return addComponentColumn { bean ->
    Icon(iconButton).apply {
      configIcon(this, bean)
      this.style.set("cursor", "pointer")
      if (tooltip != null) this.setTooltipText(tooltip)
      onLeftClick {
        execButton(bean)
      }
    }
  }.apply {
    this.isAutoWidth = false
    this.isExpand = false
    this.width = "4em"
    this.center()
    this.block()
  }
}

fun <T : Any> (@VaadinDsl Grid<T>).addColumnButton(
  iconButton: VaadinIcon,
  tooltip: String? = null,
  header: String? = null,
  configIcon: (Icon, T) -> Unit = { _, _ -> },
  execButton: (T) -> Unit = {}
): Column<T> {
  return addColumnButton(iconButton, tooltip, execButton, configIcon) {
    this.setHeader(header)
    this.isAutoWidth = false
    this.flexGrow = 0
    this.isExpand = false
    this.width = "4em"
  }
}

fun <T : Any> (@VaadinDsl Grid<T>).addColumnSeq(label: String, width: String? = null): Column<T> {
  return addColumn {
    val lista = this.list()
    lista.indexOf(it) + 1
  }.apply {
    this.textAlign = ColumnTextAlign.END
    this.setHeader(label)
    this.key = label
    this.isExpand = false
    if (width != null) {
      this.isAutoWidth = false
      this.width = width
    } else {
      this.isAutoWidth = true
    }
    this.isResizable = true
  }
}

@JvmName("columnString")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, String?>,
  header: String? = null,
  width: String? = null,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property).apply {
    this.setHeader(header ?: property.name)
    this.isExpand = false
    if (width != null) {
      this.isAutoWidth = false
      this.width = width
    } else {
      this.isAutoWidth = true
    }
    this.isResizable = true
    if (this.key == null) this.key = property.name
    this.left()
    this.block()
  }
}

@JvmName("columnBoolean")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, Boolean?>,
  header: String? = null,
  width: String? = null,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  val column = this.addComponentColumn { bean ->
    val boleanValue = property.get(bean) ?: false
    if (boleanValue) VaadinIcon.CHECK_CIRCLE_O.create()
    else VaadinIcon.CIRCLE_THIN.create()
  }
  column.isExpand = false
  if (width != null) {
    column.isAutoWidth = false
    column.width = width
  } else {
    column.isAutoWidth = true
  }
  column.isResizable = true
  column.setHeader(header ?: property.name)
  if (column.key == null) column.key = property.name
  column.center()
  column.block()
  return column
}

@JvmName("columnLocalDate")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, LocalDate?>,
  header: String? = null,
  width: String? = "120px",
  pattern: String = DATE_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, renderer = LocalDateRenderer(property, pattern)).apply {
    this.setHeader(header ?: property.name)
    this.isExpand = false
    if (width != null) {
      this.isAutoWidth = false
      this.width = width
    } else {
      this.isAutoWidth = true
    }
    this.isResizable = true
    if (this.key == null) this.key = property.name
    this.right()
    this.setComparator { a, b ->
      val dataA = property.get(a) ?: LocalDate.of(1900, 1, 1)
      val dataB = property.get(b) ?: LocalDate.of(1900, 1, 1)
      dataA.compareTo(dataB)
    }
    this.block()
  }
}

@JvmName("columnDate")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, Date?>,
  header: String? = null,
  width: String? = null,
  pattern: String = DATE_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, renderer = TextRenderer { bean ->
    val date = property.get(bean)
    date.format(pattern)
  }).apply {
    this.setHeader(header ?: property.name)
    this.isExpand = false
    if (width != null) {
      this.isAutoWidth = false
      this.width = width
    } else {
      this.isAutoWidth = true
    }
    this.isResizable = true
    if (this.key == null) this.key = property.name
    this.left()

    this.block()
  }
}

@JvmName("columnLocalTime")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, LocalTime?>,
  header: String? = null,
  width: String? = null,
  pattern: String = TIME_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, TextRenderer { bean ->
    val hora = property.get(bean)
    hora.format(pattern)
  }).apply {
    this.setHeader(header ?: property.name)
    this.isExpand = false
    if (width != null) {
      this.isAutoWidth = false
      this.width = width
    } else {
      this.isAutoWidth = true
    }
    this.isResizable = true
    if (this.key == null) this.key = property.name
    this.left()
    this.block()
  }
}

@JvmName("columnTime")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, Time?>,
  header: String? = null,
  width: String? = null,
  pattern: String = TIME_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, TextRenderer { bean ->
    val hora = property.get(bean)
    hora.format(pattern)
  }).apply {
    this.setHeader(header ?: property.name)
    this.isExpand = false
    if (width != null) {
      this.isAutoWidth = false
      this.width = width
    } else {
      this.isAutoWidth = true
    }
    this.isResizable = true
    if (this.key == null) this.key = property.name
    this.left()
    this.setComparator { a, b ->
      val dataA = property.get(a) ?: Time(0)
      val dataB = property.get(b) ?: Time(0)
      dataA.compareTo(dataB)
    }
    this.block()
  }
}

@JvmName("columnLocalDateTime")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, LocalDateTime?>,
  header: String? = null,
  width: String? = null,
  pattern: String = DATETIME_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, renderer = LocalDateTimeRenderer(property, pattern)).apply {
    this.setHeader(header ?: property.name)
    if (this.key == null) this.key = property.name
    this.isExpand = false
    if (width != null) {
      this.isAutoWidth = false
      this.width = width
    } else {
      this.isAutoWidth = true
    }
    this.isResizable = true
    this.left()
    this.setComparator { a, b ->
      val dataA = property.get(a) ?: LocalDateTime.MIN
      val dataB = property.get(b) ?: LocalDateTime.MIN
      dataA.compareTo(dataB)
    }

    this.block()
  }
}

@JvmName("columnDouble")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, Double?>,
  header: String? = null,
  width: String? = null,
  pattern: String = "#,##0.00",
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, renderer = NumberRenderer(property, DecimalFormat(pattern))).apply {
    this.setHeader(header ?: property.name)
    this.isExpand = false
    if (width != null) {
      this.isAutoWidth = false
      this.width = width
    } else {
      this.isAutoWidth = false
      this.width = "8em"
    }
    this.isResizable = true
    this.setComparator { a, b ->
      val dataA = property.get(a) ?: Double.MIN_VALUE
      val dataB = property.get(b) ?: Double.MIN_VALUE
      dataA.compareTo(dataB)
    }
    if (this.key == null) this.key = property.name
    this.right()
    this.block()
  }
}

@JvmName("columnInt")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, Int?>,
  header: String? = null,
  width: String? = null,
  pattern: String = "0",
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, renderer = NumberRenderer(property, DecimalFormat(pattern))).apply {
    this.setHeader(header ?: property.name)
    if (this.key == null) this.key = property.name
    if (width != null) {
      this.isAutoWidth = false
      this.width = width
    } else {
      this.isAutoWidth = true
      this.width = "8em"
    }
    this.isExpand = false
    this.isResizable = true
    this.setComparator { a, b ->
      val dataA = property.get(a) ?: Int.MIN_VALUE
      val dataB = property.get(b) ?: Int.MIN_VALUE
      dataA.compareTo(dataB)
    }
    this.right()
    this.block()
  }
}

fun <T : Any> (@VaadinDsl Column<T>).header(header: String): Column<T> {
  this.setHeader(header)
  return this
}

fun <T : Any> (@VaadinDsl Column<T>).width(width: String): Column<T> {
  this.width = width
  return this
}

fun <T : Any> (@VaadinDsl Column<T>).expand(expand: Boolean = true): Column<T> {
  this.isExpand = expand
  return this
}

fun <T : Any> Column<T>.right(): Column<T> {
  this.textAlign = ColumnTextAlign.END
  return this
}

fun <T : Any> Column<T>.left(): Column<T> {
  this.textAlign = ColumnTextAlign.START
  return this
}

fun <T : Any> Column<T>.center(): Column<T> {
  this.textAlign = ColumnTextAlign.CENTER
  return this
}
