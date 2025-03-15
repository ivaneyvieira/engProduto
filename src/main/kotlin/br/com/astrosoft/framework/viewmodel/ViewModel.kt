package br.com.astrosoft.framework.viewmodel

import br.com.astrosoft.framework.model.exceptions.EModelFail
import br.com.astrosoft.framework.model.exceptions.EViewModelFail
import br.com.astrosoft.framework.model.printText.TextBuffer
import br.com.astrosoft.produto.model.beans.Rota

abstract class ViewModel<V : IView>(val view: V) {
  fun <T> exec(block: () -> T) = exec(view, block)
  protected abstract fun listTab(): List<ITabView>

  fun tabsAuthorized() = listTab().filter {
    it.isAuthorized()
  }

  fun showError(msg: String) = view.showError(msg)
  fun showQuestion(msg: String, execYes: () -> Unit) = view.showQuestion(msg, execYes)
  fun showWarning(msg: String) = view.showWarning(msg)
  fun showInformation(msg: String) = view.showInformation(msg)
}

fun <T> exec(view: IView, block: () -> T): T {
  return try {
    block()
  } catch (e: EViewModelFail) {
    view.showError(e.message ?: "Erro generico")
    throw e
  } catch (e: EModelFail) {
    view.showError("Erro de banco de dados: ${e.message ?: "Erro generico"}")
    throw e
  }
}

interface IViewModelUpdate {
  fun updateView()
}

fun fail(message: String): Nothing {
  throw EViewModelFail(message)
}

interface IView {
  fun showError(msg: String)
  fun showWarning(msg: String)
  fun showInformation(msg: String)
  fun showQuestion(msg: String, execYes: () -> Unit)
  fun showReport(chave: String, report: ByteArray)
  fun showPrintText(
    text: TextBuffer,
    showPrinter: Boolean = true,
    printerUser: List<String> = emptyList(),
    rota: Rota? = null,
    loja: Int,
    actionSave: Runnable? = null,
    printEvent: (impressora: String) -> Unit
  )
}