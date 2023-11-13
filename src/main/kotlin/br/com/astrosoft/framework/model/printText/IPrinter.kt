package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.util.CupsUtils
import java.io.File

interface IPrinter {
  fun print(text: String)
}

class PrinterCups(private val printerName: String) : IPrinter {
  override fun print(text: String) {
    CupsUtils.printCups(printerName, text)
  }
}

class PrinterFile() : IPrinter {
  override fun print(text: String) {
    val file = File("/tmp/relatorio.txt")
    file.writeText(text)
  }
}