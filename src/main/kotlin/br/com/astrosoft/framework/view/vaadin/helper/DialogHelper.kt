package br.com.astrosoft.framework.view.vaadin.helper

import br.com.astrosoft.framework.view.vaadin.SubWindowPDF
import com.vaadin.flow.component.confirmdialog.ConfirmDialog
import com.vaadin.flow.component.formlayout.FormLayout

object DialogHelper {
  fun showForm(caption: String, form: FormLayout, runConfirm: (() -> Unit)) {
    ConfirmDialog().apply {
      this.setHeader(caption)
      this.setText(form)
      this.setConfirmText("Confirma")
      this.addConfirmListener {
        runConfirm()
      }
      this.setCancelable(true)
      this.setCancelText("Cancela")
      this.open()
    }
  }

  fun showError(msg: String) {
    val dialog = ConfirmDialog()
    dialog.setHeader("Erro do aplicativo")
    dialog.setText(msg)
    dialog.setConfirmText("OK")
    dialog.open()
  }

  fun showWarning(msg: String) {
    val dialog = ConfirmDialog()
    dialog.setHeader("Aviso")
    dialog.setText(msg)
    dialog.setConfirmText("OK")
    dialog.open()
  }

  fun showInformation(msg: String) {
    val dialog = ConfirmDialog()
    dialog.setHeader("Informação")
    dialog.setText(msg)
    dialog.setConfirmText("OK")
    dialog.open()
  }

  fun showReport(chave: String, report: ByteArray) {
    SubWindowPDF(chave, report).open()
  }

  fun showQuestion(msg: String, execYes: () -> Unit) {
    showQuestion(msg, execYes) {}
  }

  fun showQuestion(msg: String, execYes: () -> Unit, execNo: () -> Unit) {
    val dialog = ConfirmDialog()
    dialog.setHeader("Confirmação")
    dialog.setText(msg)

    dialog.setCancelable(true)
    dialog.setCancelText("Não")
    dialog.addCancelListener {
      execNo()
    }

    dialog.setConfirmText("Sim")
    dialog.setConfirmButtonTheme("error primary")
    dialog.addConfirmListener {
      execYes()
    }
    dialog.open()
  }
}