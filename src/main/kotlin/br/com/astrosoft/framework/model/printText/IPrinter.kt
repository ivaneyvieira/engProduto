package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.util.CupsUtils
import org.apache.http.conn.ConnectTimeoutException
import java.io.File

interface IPrinter {
  fun print(text: TextBuffer)
}

class DummyPrinter : IPrinter {
  private var text: TextBuffer? = null

  override fun print(text: TextBuffer) {
    this.text = text
  }

  fun textBuffer() = text ?: TextBuffer()
}

class PrinterCups(private val printerName: String, private val loja: Int) : IPrinter {
  override fun print(text: TextBuffer) {
    try {
      CupsUtils.printCups(printerName, text.printEspPos())
      if (printerName.startsWith("RESSU4.", ignoreCase = true)) {
        if (loja in listOf(2, 3, 5, 8))
          CupsUtils.printCups("Exp$loja.Termica", text.toString())
      }
    } catch (e: ConnectTimeoutException) {
      e.printStackTrace()
    }
  }
}

class PrinterFile : IPrinter {
  override fun print(text: TextBuffer) {
    val file = File("/tmp/relatorio.txt")
    file.writeText(text.printEspPos())
  }
}