package br.com.astrosoft.framework.view.vaadin.helper

import com.vaadin.flow.component.UI
import java.util.concurrent.Future

class UIThread(private val ui: UI, private val action: () -> Unit) : Thread() {
  private var runAcess: Future<Void>? = null

  override fun run() {
    try {
      runAcess = ui.access {
        action()
        ui.push()
      }
    } catch (e: InterruptedException) {
      e.printStackTrace()
    }
  }

  override fun start() {
    super.start()
    runAcess?.get()
  }
}