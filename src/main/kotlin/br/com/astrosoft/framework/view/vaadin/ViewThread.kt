package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.viewmodel.DataViewThread
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.progressbar.ProgressBar

class ViewThread<T> private constructor(
  private val ui: UI,
  private val dataViewThread: DataViewThread<T>
) : Thread() {
  override fun run() {
    showLoad()
    val block = dataViewThread.block
    if (block != null) {
      val result = block()
      val update = dataViewThread.update
      ui.access {
        update?.invoke(result)
      }
    }
    hideLoad()
  }

  private fun showLoad() {
    ui.access {
      ViewThread.showLoad()
    }
  }

  private fun hideLoad() {
    ui.access {
      ViewThread.hideLoad()
    }
  }

  companion object {
    private val threads = mutableListOf<ViewThread<*>>()

    private var loadIndicator: ProgressBar? = null

    fun registrerLoad(loadIndicator: ProgressBar) {
      this.loadIndicator = loadIndicator
    }

    fun showLoad() {
      loadIndicator?.isVisible = true
    }

    fun hideLoad() {
      loadIndicator?.isVisible = false
    }

    fun <T> execAsync(ui: UI, blockDataViewThread: DataViewThread<T>.() -> Unit) {
      val dataViewThread = DataViewThread<T>()
      blockDataViewThread(dataViewThread)
      execAsync(ui, dataViewThread)
    }

    fun <T> execAsync(ui: UI, dataViewThread: DataViewThread<T>) {
      val thread = ViewThread(ui, dataViewThread)
      interruptAll()
      threads.add(thread)
      thread.start()
    }

    private fun interruptAll() {
      threads.forEach {
        it.hideLoad()
        it.interrupt()
      }
      threads.clear()
    }
  }
}



