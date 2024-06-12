package br.com.astrosoft.framework.viewmodel

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.produto.model.beans.Rota

interface ITabView {
  fun isAuthorized(): Boolean
  val label: String
  fun updateComponent()
  fun printerPreview(
    showPrinter: Boolean = true,
    rota: Rota? = null,
    loja: Int,
    printEvent: (impressora: String) -> Unit = {}
  ): IPrinter

  fun execThread(block: () -> Unit)
}
