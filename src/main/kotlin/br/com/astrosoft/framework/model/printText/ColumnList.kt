package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.rpad
import java.text.DecimalFormat
import kotlin.reflect.KProperty1

class ColumnList<T> {
  private val itens = mutableListOf<Column<T, *>>()

  @JvmName("columnString")
  fun column(property: KProperty1<T, String?>, header: String, size: Int, lineBreak: Boolean = false) {
    val column = Column(header, size, lineBreak, property) { str ->
      str.rpad(size, " ")
    }
    itens.add(column)
  }

  @JvmName("columnDouble")
  fun column(
    property: KProperty1<T, Double?>,
    header: String,
    size: Int,
    format: String = "#,##0.00",
    lineBreak: Boolean = false
  ) {
    val decimalFormat = DecimalFormat(format)
    val column = Column(header, size, lineBreak, property) { number ->
      decimalFormat.format(number).lpad(size, " ")
    }
    itens.add(column)
  }

  @JvmName("columnInt")
  fun column(
    property: KProperty1<T, Int?>,
    header: String,
    size: Int,
    format: String = "#,##0",
    lineBreak: Boolean = false
  ) {
    val decimalFormat = DecimalFormat(format)
    val column = Column(header, size, lineBreak, property) { number ->
      decimalFormat.format(number).lpad(size, " ")
    }
    itens.add(column)
  }

  fun montaLinha(process: (Column<T, *>) -> String): String {
    return itens.joinToString(separator = "") { col ->
      val lineBreak = if (col.lineBreak) "\n" else " "
      val linha = process(col)
      "$linha$lineBreak"
    }
  }
}

data class Column<T, V>(
  val header: String,
  val size: Int,
  val lineBreak: Boolean,
  val process: T.() -> V,
  val posProcess: (V) -> String
) {
  val columnText
    get() = header.rpad(size, "_")

  fun dataText(value: T) = posProcess(process(value))
}