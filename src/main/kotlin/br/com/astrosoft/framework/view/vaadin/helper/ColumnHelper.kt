package br.com.astrosoft.framework.view.vaadin.helper

import br.com.astrosoft.framework.util.DATETIME_PATTERN
import br.com.astrosoft.framework.util.DATE_PATTERN
import br.com.astrosoft.framework.util.TIME_PATTERN
import br.com.astrosoft.framework.util.format
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.column
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.kaributools.addColumnFor
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.Column
import com.vaadin.flow.component.grid.GridVariant.*
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.data.renderer.LocalDateRenderer
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer
import com.vaadin.flow.data.renderer.NumberRenderer
import com.vaadin.flow.data.renderer.TextRenderer
import com.vaadin.flow.function.ValueProvider
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
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
    val iconCreate = iconButton.create().apply {
      configIcon(this, bean)
    }
    Button().apply {
      this.icon = iconCreate
      this.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL)
      this.style.set("cursor", "pointer")
      if (tooltip != null) this.setTooltipText(tooltip)
      this.onClick {
        execButton(bean)
      }
    }
  }.apply {
    this.isResizable = true
    this.isAutoWidth = true
    this.flexGrow = 0
    this.isExpand = false
    this.center()
    this.width = "2em"
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
    this.isAutoWidth = true
    this.flexGrow = 0
    this.isExpand = false
  }
}

private fun <T : Any> (@VaadinDsl Grid<T>).addColumnDownload2(
  iconButton: VaadinIcon,
  tooltip: String? = null,
  configIcon: (Icon, T) -> Unit = { _, _ -> },
  filename: (T) -> String,
  execButton: (T) -> ByteArray? = { null },
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return addComponentColumn { bean ->
    val iconCreate = iconButton.create().apply {
      configIcon(this, bean)
    }
    LazyDownloadButton(iconCreate, { filename(bean) }, {
      val bytes = execButton(bean)
      ByteArrayInputStream(bytes)
    }).apply {
      this.icon = iconCreate
      this.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL)
      this.style.set("cursor", "pointer")
      if (tooltip != null) this.setTooltipText(tooltip)
    }
  }.apply {
    this.isResizable = true
    this.isAutoWidth = true
    this.flexGrow = 0
    this.isExpand = false
    this.center()
    this.width = "2em"
    this.block()
  }
}

fun <T : Any> (@VaadinDsl Grid<T>).addColumnDownload(
  iconButton: VaadinIcon,
  tooltip: String? = null,
  header: String? = null,
  configIcon: (Icon, T) -> Unit = { _, _ -> },
  filename: (T) -> String,
  execButton: (T) -> ByteArray? = { null }
): Column<T> {
  return addColumnDownload2(iconButton, tooltip, configIcon, filename, execButton) {
    this.setHeader(header)
    this.isAutoWidth = true
    this.flexGrow = 0
    this.isExpand = false
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

@JvmName("columnProvider")
fun <T : Any, V : Any> (@VaadinDsl Grid<T>).columnGrid(
  valueProvider: ValueProvider<T, V?>,
  key: String? = null,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.column(valueProvider) {
    this.key = key
    this.setHeader(header ?: "")
    if (isExpand) {
      this.isExpand = true
    } else {
      this.isExpand = false
      if (width != null) {
        this.isAutoWidth = false
        this.width = width
      } else {
        this.isAutoWidth = true
      }
    }
    this.isResizable = true
    this.left()
    this.block()
  }
}

@JvmName("columnString")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, String?>,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property).apply {
    this.setHeader(header ?: property.name)
    this.isResizable = true
    if (isExpand) {
      this.isExpand = true
    } else {
      this.isExpand = false
      if (width != null) {
        this.isAutoWidth = false
        this.width = width
      } else {
        this.isAutoWidth = true
        this.setFlexGrow(0)
      }
    }
    if (this.key == null) this.key = property.name
    this.left()
    this.block()
  }
}

@JvmName("columnSet")
fun <T : Any> (@VaadinDsl Grid<T>).columnGrid(
  property: KProperty1<T, Set<Any>>,
  header: String? = null,
  width: String? = null,
  isExpand: Boolean = false,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property = property, renderer = SetRenderer(property)).apply {
    this.setHeader(header ?: property.name)
    if (isExpand) {
      this.isExpand = true
    } else {
      this.isExpand = false
      if (width != null) {
        this.isAutoWidth = false
        this.width = width
      } else {
        this.isAutoWidth = true
      }
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
  val column: Column<T> = this.addComponentColumn { bean ->
    val boleanValue = property.get(bean) ?: false
    if (boleanValue) VaadinIcon.CHECK_CIRCLE_O.create()
    else VaadinIcon.CIRCLE_THIN.create()
  }
  if (isExpand) {
    column.isExpand = true
  } else {
    column.isExpand = false
    if (width != null) {
      column.isAutoWidth = false
      column.width = width
    } else {
      column.isAutoWidth = true
    }
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
  isExpand: Boolean = false,
  pattern: String = DATE_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, renderer = LocalDateRenderer({
    property.get(it)
  }, pattern)).apply {
    this.setHeader(header ?: property.name)

    if (isExpand) {
      this.isExpand = true
    } else {
      this.isExpand = false
      if (width != null) {
        this.isAutoWidth = false
        this.width = width
      } else {
        this.isAutoWidth = true
      }
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
  isExpand: Boolean = false,
  pattern: String = DATE_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, renderer = TextRenderer { bean ->
    val date = property.get(bean)
    date.format(pattern)
  }).apply {
    this.setHeader(header ?: property.name)
    if (isExpand) {
      this.isExpand = true
    } else {
      this.isExpand = false
      if (width != null) {
        this.isAutoWidth = false
        this.width = width
      } else {
        this.isAutoWidth = true
      }
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
  isExpand: Boolean = false,
  pattern: String = TIME_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, TextRenderer { bean ->
    val hora = property.get(bean)
    hora.format(pattern)
  }).apply {
    this.setHeader(header ?: property.name)
    if (isExpand) {
      this.isExpand = true
    } else {
      this.isExpand = false
      if (width != null) {
        this.isAutoWidth = false
        this.width = width
      } else {
        this.isAutoWidth = true
      }
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
  isExpand: Boolean = false,
  pattern: String = TIME_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, TextRenderer { bean ->
    val hora = property.get(bean)
    hora.format(pattern)
  }).apply {
    this.setHeader(header ?: property.name)
    if (isExpand) {
      this.isExpand = true
    } else {
      this.isExpand = false
      if (width != null) {
        this.isAutoWidth = false
        this.width = width
      } else {
        this.isAutoWidth = true
      }
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
  isExpand: Boolean = false,
  pattern: String = DATETIME_PATTERN,
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, renderer = LocalDateTimeRenderer({
    property.get(it)
  }, pattern)).apply {
    this.setHeader(header ?: property.name)
    if (this.key == null) this.key = property.name
    if (isExpand) {
      this.isExpand = true
    } else {
      this.isExpand = false
      if (width != null) {
        this.isAutoWidth = false
        this.width = width
      } else {
        this.isAutoWidth = true
      }
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
  isExpand: Boolean = false,
  pattern: String = "#,##0.00",
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, renderer = NumberRenderer({
    property.get(it)
  }, DecimalFormat(pattern))).apply {
    this.setHeader(header ?: property.name)
    this.isResizable = true
    if (isExpand) {
      this.isExpand = true
    } else {
      this.isExpand = false
      this.setFlexGrow(0)
      this.isAutoWidth = false
      if (width != null) {
        this.width = width
      } else {
        this.isAutoWidth = true
      }
    }
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
  isExpand: Boolean = false,
  pattern: String = "0",
  block: (@VaadinDsl Column<T>).() -> Unit = {}
): Column<T> {
  return this.addColumnFor(property, renderer = NumberRenderer({
    property.get(it)
  }, DecimalFormat(pattern))).apply {
    this.setHeader(header ?: property.name)
    if (this.key == null) this.key = property.name
    if (isExpand) {
      this.isExpand = true
    } else {
      this.isExpand = false
      if (width != null) {
        this.isAutoWidth = false
        this.width = width
      } else {
        this.isAutoWidth = true
        this.width = "120px"
      }
    }
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
  this.isAutoWidth = false
  this.isExpand = false
  this.setFlexGrow(0)
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

fun <T : Any> Grid<T>.format() {
  this.addThemeVariants(
    LUMO_COMPACT,
    LUMO_ROW_STRIPES,
    LUMO_COLUMN_BORDERS,
    LUMO_WRAP_CELL_CONTENT
  )
}