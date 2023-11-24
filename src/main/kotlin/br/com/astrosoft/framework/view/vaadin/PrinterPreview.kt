package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper

class PrinterPreview(val printerUser: String, val printEvent: (impressora: String) -> Unit) : IPrinter {
  override fun print(text: String) {
    DialogHelper.showPrintText(text, printerUser, printEvent)
  }
}