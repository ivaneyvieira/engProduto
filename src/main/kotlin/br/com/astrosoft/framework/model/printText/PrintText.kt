package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_128
import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_HEIGHT
import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_WIDTH
import br.com.astrosoft.framework.model.printText.EscPosConst.NEGRITO_OFF
import br.com.astrosoft.framework.model.printText.EscPosConst.NEGRITO_ON
import br.com.astrosoft.framework.model.printText.EscPosConst.SET_FONT_SMALL
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

  protected open fun groupBotton(beanDetail: T): String {
    return ""
  }

  fun print(dados: List<T>, printer: IPrinter) {
    dados.firstOrNull()?.let { bean ->
      textBuffer.inicializePrint()
      printTitle(bean)

      printHeader()

      val groupDados = dados.groupBy { groupBotton(it) }

      groupDados.forEach { (group, list) ->
        list.forEach { beanDetail ->
          if (group != "") {
            textBuffer.println("")
            textBuffer.println(group)
          }
          printDetail(beanDetail)
        }
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
      val textNeg = if (negrito) textCenter.negrito() else {
        textCenter.replace("<B>", NEGRITO_ON).replace("</B>", NEGRITO_OFF).negritoOff()
      }
      return@let textNeg
    })
  }

  protected fun printLine(character: Char = '-') {
    textBuffer.println(character.toString().repeat(widthPage))
  }

  protected fun String?.center(width: Int): String {
    if (this == null) return " ".repeat(width)
    val margem = (width - this.length) / 2
    val margemEsquerda = if (margem < 0) 0 else margem
    val parte1 = " ".repeat(margemEsquerda) + this
    val resto = width - parte1.length
    val margemDireita = if (resto < 0) 0 else resto
    val text = parte1 + " ".repeat(margemDireita)
    return if (text.length >= width) text.substring(0, width) else text.padEnd(width, ' ')
  }
}

