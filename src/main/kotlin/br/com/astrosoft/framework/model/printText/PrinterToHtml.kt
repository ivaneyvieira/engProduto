package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_128
import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_HEIGHT
import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_WIDTH
import br.com.astrosoft.framework.model.printText.EscPosConst.PAPPER_CUT
import br.com.astrosoft.framework.model.printText.EscPosConst.SET_FONT_SMALL

object PrinterToHtml {
  private val FONT_SIZE = 3
  fun toHtml(text: String): String {
    val html =
        text


    val htmlFormat = html.lines().joinToString(separator = "<br>") { linha ->
      "<code>$linha</code>"
    }

    return htmlFormat
  }


}