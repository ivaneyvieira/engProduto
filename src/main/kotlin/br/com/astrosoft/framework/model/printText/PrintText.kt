package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_128
import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_HEIGHT
import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_WIDTH
import br.com.astrosoft.framework.model.printText.EscPosConst.NEGRITO_OFF
import br.com.astrosoft.framework.model.printText.EscPosConst.NEGRITO_ON
import br.com.astrosoft.framework.model.printText.EscPosConst.SET_FONT_SMALL
import br.com.astrosoft.produto.model.beans.ProdutoPedidoTransf
import kotlin.reflect.KProperty1

abstract class PrintText<T>(val widthPage: Int = 64) {
  private val columns: ColumnList<T> = ColumnList()
  private val textBuffer = TextBuffer()

  @JvmName("columnString")
  fun column(property: KProperty1<T, String?>, header: String, size: Int, lineBreak: Boolean = false) {
    columns.column(property, header, size, lineBreak)
  }

  @JvmName("columnDouble")
  fun column(
    property: KProperty1<T, Double?>,
    header: String,
    size: Int,
    format: String = "#,##0.00",
    lineBreak: Boolean = false
  ) {
    columns.column(property, header, size, format, lineBreak)
  }

  @JvmName("columnInt")
  fun column(
    property: KProperty1<T, Int?>,
    header: String,
    size: Int,
    format: String = "#,##0",
    lineBreak: Boolean = false
  ) {
    columns.column(property, header, size, format, lineBreak)
  }

  private fun header() = columns.montaLinha { col ->
    col.columnText.negrito()
  }

  private fun detail(value: T) = columns.montaLinha { col ->
    col.dataText(value)
  }

  fun print(dados: List<T>, printer: IPrinter) {
    dados.firstOrNull()?.let { bean ->
      textBuffer.inicializePrint()
      printTitle(bean)

      printHeader()

      dados.forEach { beanDetail ->
        printDetail(beanDetail)
      }

      printSumary()

      textBuffer.finalizePrint()
      printer.print(textBuffer.printText())
    }
  }

  open fun printSumary() {
    println("")
  }

  protected fun String.barras(): String {
    val stringBuffer = StringBuilder()
    stringBuffer
      //Height
      .append(BARCODE_HEIGHT)
      //Width
      .append(BARCODE_WIDTH)
      //Barcode 128
      .append(BARCODE_128)
      .append(this.length.toChar())
      .append(this)
    return stringBuffer.toString()
  }

  protected fun String.negrito(): String {
    return "$NEGRITO_ON${this}$NEGRITO_OFF"
  }

  protected fun String.negritoOff(): String {
    return "$SET_FONT_SMALL${this}"
  }

  private fun printDetail(bean: T) {
    textBuffer.println(detail(bean))
  }

  private fun printHeader() {
    textBuffer.println(header())
  }

  open fun printTitle(bean: T) {
    textBuffer.println("")
  }

  protected fun println(text: String, negrito: Boolean = false, center: Boolean = false) {
    textBuffer.println(text.let { textOrig ->
      val textCenter = if (center) {
        val margem = (widthPage - textOrig.length) / 2
        " ".repeat(margem) + textOrig
      } else textOrig
      val textNeg = if (negrito) textCenter.negrito() else textCenter.negritoOff()
      return@let textNeg
    })
  }

  protected fun printLine(character: Char) {
    textBuffer.println(character.toString().repeat(widthPage))
  }
}

