package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.rpad
import java.text.DecimalFormat
import kotlin.reflect.KProperty1

class ColumnList<T> {
  private val itens = mutableListOf<Column<T, *>>()

  @JvmName("columnString")
  fun column(
    property: KProperty1<T, String?>,
    header: String,
    size: Int,
    lineBreak: Boolean = false,
    expand: Boolean = true
  ) {
    val column = Column(header, size, lineBreak, expand, property) { str ->
      str.rpad(size, " ").let {
        if (expand) it.expand() else it
      }
    }
    itens.add(column)
  }

  @JvmName("columnDouble")
  fun column(
    property: KProperty1<T, Double?>,
    header: String,
    size: Int,
    format: String = "#,##0.00",
    lineBreak: Boolean = false,
    expand: Boolean = true
  ) {
    val decimalFormat = DecimalFormat(format)
    val column = Column(header, size, lineBreak, expand, property) { number ->
      decimalFormat.format(number).lpad(size, " ").let {
        if (expand) it.expand() else it
      }
    }
    itens.add(column)
  }

  @JvmName("columnInt")
  fun column(
    property: KProperty1<T, Int?>,
    header: String,
    size: Int,
    format: String = "#,##0",
    lineBreak: Boolean = false,
    expand: Boolean = false,
  ) {
    val decimalFormat = DecimalFormat(format)
    val column = Column(
      header = header,
      size = size,
      lineBreak = lineBreak,
      process = property,
      expand = expand
    ) { number ->
      val numberStr = if (number == null) "" else decimalFormat.format(number)
      numberStr.lpad(size, " ").let { text ->
        if (expand) text.expand() else text
      }
    }
    itens.add(column)
  }

  fun montaLinha(process: (Column<T, *>) -> String): String {
    return itens.joinToString(separator = "") { col ->
      val lineBreak = if (col.lineBreak) "\n" else " "
      val linha = process(col)
      val dados = "$linha$lineBreak"
      val length = dados.length
      println(length)
      dados
    }.trimEnd()
  }
}

data class Column<T, V>(
  val header: String,
  val size: Int,
  val lineBreak: Boolean,
  val expand: Boolean,
  val process: T.() -> V,
  val posProcess: (V) -> String
) {
  val columnText: String
    get() {
      val tamanho = if (expand) size * 2 else size
      return if (header.trim() == "") {
        header.rpad(tamanho, " ")
      } else {
        header.rpad(tamanho, "_")
      }
    }

  fun dataText(value: T) = posProcess(process(value))
}