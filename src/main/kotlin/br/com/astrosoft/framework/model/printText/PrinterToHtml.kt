package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_128
import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_HEIGHT
import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_WIDTH
import br.com.astrosoft.framework.model.printText.EscPosConst.EXPANDIDON_OFF
import br.com.astrosoft.framework.model.printText.EscPosConst.EXPANDIDON_ON
import br.com.astrosoft.framework.model.printText.EscPosConst.EXPANDIDO_OFF
import br.com.astrosoft.framework.model.printText.EscPosConst.EXPANDIDO_ON
import br.com.astrosoft.framework.model.printText.EscPosConst.NEGRITO_OFF
import br.com.astrosoft.framework.model.printText.EscPosConst.NEGRITO_ON
import br.com.astrosoft.framework.model.printText.EscPosConst.PAPPER_CUT
import br.com.astrosoft.framework.model.printText.EscPosConst.SET_FONT_SMALL

object PrinterToHtml {
  private val FONT_SIZE = 3
  fun toHtml(text: String): String {
    val html =
        text
          .replace(" ", "&nbsp;")
          .removerInicializer()
          .removerCodigoBarras()
          .removeFinalize()
          .removerNegrito()
          .removerExpandido()

    val htmlFormat = html.lines().joinToString(separator = "<br>") { linha ->
      "<code><font size=$FONT_SIZE>$linha</font></code>"
    }

    return htmlFormat
  }

  private fun String.removerInicializer(): String {
    val padrao = "^$SET_FONT_SMALL".toRegex()
    return this.replace(padrao, "")
  }

  private fun String.removeFinalize(): String {
    return this.replace(PAPPER_CUT, "")
  }

  private fun String.removerCodigoBarras(): String {
    val padrao3 = "$BARCODE_128..".toRegex()
    return this.replace(BARCODE_HEIGHT, "").replace(BARCODE_WIDTH, "")
      .replace(padrao3, "")
  }

  private fun String.removerNegrito(): String {
    return this.replace(NEGRITO_ON, "<strong>")
      .replace(NEGRITO_OFF, "</strong>")
  }

  private fun String.removerExpandido(): String {
    return this.replace(EXPANDIDON_ON, "<strong><font size=${FONT_SIZE * 2}>")
      .replace(EXPANDIDON_OFF, "</font></strong>")
      .replace(EXPANDIDO_ON, "<font size=${FONT_SIZE * 2}>")
      .replace(EXPANDIDO_OFF, "</font>")
  }
}