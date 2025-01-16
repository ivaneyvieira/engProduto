package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.model.printText.EscPosConst.EXPANDIDON_OFF
import br.com.astrosoft.framework.model.printText.EscPosConst.EXPANDIDON_ON
import br.com.astrosoft.framework.model.printText.EscPosConst.EXPANDIDO_OFF
import br.com.astrosoft.framework.model.printText.EscPosConst.EXPANDIDO_ON
import br.com.astrosoft.framework.model.printText.EscPosConst.NEGRITO_OFF
import br.com.astrosoft.framework.model.printText.EscPosConst.NEGRITO_ON
import br.com.astrosoft.framework.model.printText.EscPosConst.PAPPER_CUT
import br.com.astrosoft.framework.model.printText.EscPosConst.SET_FONT_SMALL

class TextBuffer {
  private val buffer = StringBuilder()
  private val FONT_SIZE = 3

  fun textBuf(): String {
    return buffer.toString()
  }

  fun println(text: String) {
    buffer.append(text).appendLine()
  }

  fun inicializePrint() {
    buffer.clear()
    buffer.append(SET_FONT_SMALL)
  }

  fun finalizePrint() {
    buffer.append(PAPPER_CUT)
  }

  fun printEspPos() = buffer.toString()
    .replace("<B>", NEGRITO_ON)
    .replace("</B>", NEGRITO_OFF)
    .replace("<N>", SET_FONT_SMALL)
    .replace("</N>", "")
    .replace("<E>", EXPANDIDO_ON)
    .replace("</E>", EXPANDIDO_OFF)
    .replace("<EB>", EXPANDIDON_ON)
    .replace("</EB>", EXPANDIDON_OFF)

  fun printHtml() = buffer.toString()
    .removerInicializer()
    .removerCodigoBarras()
    .removeFinalize()
    .replace(" ", "&nbsp;")
    .replace("<B>", "<strong><font size=${FONT_SIZE}>")
    .replace("</B>", "</font></strong>")
    .replace("<N>", "<font size=${FONT_SIZE}>")
    .replace("</N>", "</font>")
    .replace("<E>", "<font size=${FONT_SIZE * 2}>")
    .replace("</E>", "</font>")
    .replace("<EB>", "<strong><font size=${FONT_SIZE * 2}>")
    .replace("</EB>", "</font></strong>")

  private fun String.removerInicializer(): String {
    val padrao = "^$SET_FONT_SMALL".toRegex()
    return this.replace(padrao, "")
  }

  private fun String.removeFinalize(): String {
    return this.replace(PAPPER_CUT, "")
  }

  private fun String.removerCodigoBarras(): String {
    val padrao3 = "${EscPosConst.BARCODE_128}..".toRegex()
    return this.replace(EscPosConst.BARCODE_HEIGHT, "")
      .replace(EscPosConst.BARCODE_WIDTH, "")
      .replace(padrao3, "")
  }
}

