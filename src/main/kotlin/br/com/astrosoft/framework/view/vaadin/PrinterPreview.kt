package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.TextBuffer
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.produto.model.beans.Rota

class PrinterPreview(
  val showPrinter: Boolean = true,
  val printerUser: List<String>,
  val rota: Rota?,
  val loja: Int,
  val printEvent: (impressora: String) -> Unit
) : IPrinter {
  override fun print(text: TextBuffer) {
    DialogHelper.showPrintText(text, showPrinter, printerUser, rota, loja, printEvent)
  }
}