package br.com.astrosoft.framework.viewmodel

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.view.vaadin.SubWindowPrinter
import br.com.astrosoft.produto.model.beans.Rota

interface ITabView {
  fun isAuthorized(): Boolean
  val label: String
  fun updateComponent()
  fun printerPreview(
    showPrinter: Boolean = true,
    rota: Rota? = null,
    loja: Int = 0,
    showPrintBunton: Boolean = true,
    actionSave: ((SubWindowPrinter) -> Unit)? = null,
    printEvent: (impressora: String) -> Unit = {}
  ): IPrinter

  fun execThread(block: () -> Unit)
}
