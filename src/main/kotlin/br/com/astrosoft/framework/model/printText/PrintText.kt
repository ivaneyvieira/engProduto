package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.rpad
import java.text.DecimalFormat
import kotlin.reflect.KProperty1

abstract class PrintText<T> {
  private val columns = mutableListOf<Column<T, *>>()

  @JvmName("columnString")
  fun column(property: KProperty1<T, String?>, header: String, size: Int, lineBreak: Boolean = false): PrintText<T> {
    val column = Column(header, size, lineBreak, property) { str ->
      str.rpad(size, " ")
    }
    columns.add(column)
    return this
  }

  @JvmName("columnDouble")
  fun column(
    property: KProperty1<T, Double?>,
    header: String,
    size: Int,
    format: String = "#,##0.00",
    lineBreak: Boolean = false
  ): PrintText<T> {
    val decimalFormat = DecimalFormat(format)
    val column = Column(header, size, lineBreak, property) { number ->
      decimalFormat.format(number).lpad(size, " ")
    }
    columns.add(column)
    return this
  }

  @JvmName("columnInt")
  fun column(
    property: KProperty1<T, Int?>,
    header: String,
    size: Int,
    format: String = "#,##0",
    lineBreak: Boolean = false
  ): PrintText<T> {
    val decimalFormat = DecimalFormat(format)
    val column = Column(header, size, lineBreak, property) { number ->
      decimalFormat.format(number).lpad(size, " ")
    }
    columns.add(column)
    return this
  }

  private fun header() = montaLinha { col ->
    col.columnText.negrito()
  }

  private fun detail(value: T) = montaLinha { col ->
    col.dataText(value)
  }

  fun print(dados: List<T>, printer: IPrinter) {
    dados.firstOrNull()?.let { bean ->
      val text = StringBuilder()
      inicialize(text)
      printTitle(text, bean)

      printHeader(text)

      dados.forEach { beanDetail ->
        printDetail(text, beanDetail)
      }

      sumary(text)

      finalize(text)
      printer.print(text.toString())
    }
  }

  open fun sumary(text: StringBuilder) {
    text.line("")
  }

  private fun inicialize(text: StringBuilder) {
    text.append(0x1b.toChar()).append(0x21.toChar()).append(0x01.toChar())
  }

  protected fun String.barras(): String {
    val stringBuffer = StringBuilder()
    stringBuffer
      .append(0x1d.toChar())
      .append(0x68.toChar())
      .append(0x50.toChar())
      .append(0x1d.toChar())
      .append(0x77.toChar())
      .append(0x04.toChar())
      .append(0x1d.toChar())
      .append(0x6b.toChar())
      .append(0x49.toChar())
      .append(this.length.toChar())
      .append(this)
    return stringBuffer.toString()
  }

  protected fun String.negrito(): String {
    val stringBuffer = StringBuilder()
    stringBuffer.append(0x1b.toChar())
      .append(0x45.toChar())
      .append(0x01.toChar())
      .append(this)
      .append(0x1b.toChar())
      .append(0x45.toChar())
      .append(0x00.toChar())
    return stringBuffer.toString()
  }

  private fun finalize(text: StringBuilder) {
    text.append(0x0a.toChar()).append(0x0a.toChar()).append(0x0a.toChar()).append(0x1b.toChar()).append(0x69.toChar())
  }

  private fun printDetail(text: StringBuilder, bean: T) {
    text.line(detail(bean))
  }

  private fun printHeader(text: StringBuilder) {
    text.line(header())
  }

  private fun printTitle(text: StringBuilder, bean: T) {
    titleLines(bean).forEach { line ->
      text.line(line.negrito())
    }
  }

  protected abstract fun titleLines(bean: T): List<String>

  protected fun StringBuilder.line(text: String): StringBuilder {
    this.append(0x1b.toChar()).append(0x21.toChar()).append(0x01.toChar())
    this.append(text).appendLine()
    return this
  }

  private fun montaLinha(process: (Column<T, *>) -> String): String {
    return columns.joinToString(separator = "") { col ->
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