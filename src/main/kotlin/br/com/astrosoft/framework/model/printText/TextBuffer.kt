package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.model.printText.EscPosConst.INICIALIZE_PRINT
import br.com.astrosoft.framework.model.printText.EscPosConst.SET_FONT_SMALL
import br.com.astrosoft.framework.model.printText.EscPosConst.PAPPER_CUT

class TextBuffer {
  private val buffer = StringBuilder()

  fun println(text: String) {
    buffer.append(text).appendLine()
  }

  fun inicializePrint() {
    buffer.clear()
    buffer.append(INICIALIZE_PRINT)
    buffer.append(SET_FONT_SMALL)
  }

  fun finalizePrint() {
    buffer.append(PAPPER_CUT)
  }

  fun printText() = buffer.toString()
}