package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_128
import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_HEIGHT
import br.com.astrosoft.framework.model.printText.EscPosConst.BARCODE_WIDTH
import br.com.astrosoft.framework.model.printText.EscPosConst.INICIALIZE_PRINT
import br.com.astrosoft.framework.model.printText.EscPosConst.NEGRITO_OFF
import br.com.astrosoft.framework.model.printText.EscPosConst.NEGRITO_ON
import br.com.astrosoft.framework.model.printText.EscPosConst.PAPPER_CUT
import br.com.astrosoft.framework.model.printText.EscPosConst.SET_FONT_SMALL

object PrinterToHtml {
  fun toHtml(text: String): String {
    val html =
        text.removerInicializer()
          .removerCodigoBarras()
          .removeFinalize()
          .removerNegrito()
          .removerExpandido()
          .replace("\n", "<br>")
          //.replace(" ", "&nbsp;")

    return "<pre>$html</pre>"
  }

  private fun String.removerInicializer(): String {
    return this.replace(SET_FONT_SMALL, "").replace(INICIALIZE_PRINT, "")
  }

  private fun String.removeFinalize(): String {
    return this.replace(PAPPER_CUT, "")
  }

  private fun String.removerCodigoBarras(): String {
    val padrao3 = "$BARCODE_128..".toRegex()
    return this.replace(BARCODE_HEIGHT, "").replace(BARCODE_WIDTH, "").replace(padrao3, "")
  }

  private fun String.removerNegrito(): String {
    return this.replace(NEGRITO_ON, "<strong>").replace(NEGRITO_OFF, "</strong>")
  }

  private fun String.removerExpandido(): String {
    return this
  }
}