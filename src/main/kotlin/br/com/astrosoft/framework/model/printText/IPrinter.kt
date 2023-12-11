package br.com.astrosoft.framework.model.printText

import br.com.astrosoft.framework.util.CupsUtils
import org.apache.http.conn.ConnectTimeoutException
import java.io.File

interface IPrinter {
  fun print(text: String)
}

class DummyPrinter : IPrinter {
  private val buffer = StringBuffer()

  override fun print(text: String) {
    buffer.append(text)
    buffer.append("\n")
  }

  fun text() = buffer.toString()
}

class PrinterCups(private val printerName: String) : IPrinter {
  override fun print(text: String) {
    try {
      CupsUtils.printCups(printerName, text)
    } catch (e: ConnectTimeoutException) {
      e.printStackTrace()
    }
  }
}

class PrinterFile : IPrinter {
  override fun print(text: String) {
    val file = File("/tmp/relatorio.txt")
    file.writeText(text)
  }
}