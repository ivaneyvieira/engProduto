package br.com.astrosoft.framework.viewmodel

import br.com.astrosoft.framework.model.printText.IPrinter

interface ITabView {
  fun isAuthorized(): Boolean
  val label: String
  fun updateComponent()
  fun printerPreview(): IPrinter
}
