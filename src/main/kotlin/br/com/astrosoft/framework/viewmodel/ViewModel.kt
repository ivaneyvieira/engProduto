package br.com.astrosoft.framework.viewmodel

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
  fun showReport(chave: String, report: ByteArray) = view.showReport(chave, report)
}

fun <T> exec(view: IView, block: () -> T): T {
  return try {
    block()
  } catch (e: EViewModelFail) {
    view.showError(e.message ?: "Erro generico")
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
  fun showPrintText(text: String, printerUser: String, printEvent: (impressora: String) -> Unit)
}