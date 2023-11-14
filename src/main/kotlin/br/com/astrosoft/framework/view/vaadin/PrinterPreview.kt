package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper

class PrinterPreview: IPrinter {
  override fun print(text: String) {
    DialogHelper.showPrintText(text)
  }
}