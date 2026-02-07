package br.com.astrosoft.framework.view.vaadin.helper

import br.com.astrosoft.framework.model.printText.TextBuffer
import br.com.astrosoft.framework.view.vaadin.SubWindowPDF
import br.com.astrosoft.framework.view.vaadin.SubWindowPrinter
import br.com.astrosoft.framework.view.vaadin.SubWindowView
import br.com.astrosoft.produto.model.beans.Rota
import com.github.mvysny.karibudsl.v10.html
import com.vaadin.flow.component.confirmdialog.ConfirmDialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout

object DialogHelper {
  fun showForm(caption: String, form: FormLayout, runConfirm: () -> Unit) {
    ConfirmDialog().apply {
      this.setHeader(caption)
      this.setText(form)
      this.setConfirmText("Confirma")
      this.addConfirmListener {
        runConfirm()
      }
      this.width = form.width
      form.setWidthFull()
      this.setCancelable(true)
      this.setCancelText("Cancela")
      this.open()
    }
  }

  fun showForm(caption: String, form: FormLayout) {
    ConfirmDialog().apply {
      this.setHeader(caption)
      this.setText(form)
      this.isCloseOnEsc = true
      this.setConfirmText("Ok")
      this.width = form.width
      form.setWidthFull()
      this.open()
    }
  }

  fun showForm(caption: String, form: VerticalLayout) {
    ConfirmDialog().apply {
      this.setHeader(caption)
      this.setText(form)
      this.isCloseOnEsc = true
      this.setConfirmText("Ok")
      this.width = form.width
      form.setWidthFull()
      this.open()
    }
  }

  fun showError(msg: String) {
    ConfirmDialog().apply {
      this.setHeader("Erro do aplicativo")
      this.setText(msg)
      this.setConfirmText("OK")
      this.open()
    }
  }

  fun showWarning(msg: String) {
    ConfirmDialog().apply {
      this.setHeader("Aviso")
      this.setText(msg)
      this.setConfirmText("OK")
      this.open()
    }
  }

  fun showInformation(msg: String, title: String = "Informação") {
    ConfirmDialog().apply {
      this.setHeader(title)
      this.setText(Div().apply {
        this.html(msg)
      })
      this.setConfirmText("OK")
      this.open()
    }
  }

  fun showReport(chave: String, report: ByteArray) {
    SubWindowPDF(chave, report).open()
  }

  fun showFile(title: String, fileName: String, report: ByteArray) {
    val dlg = SubWindowView(fileName, report)
    dlg.headerTitle = title
    dlg.open()
  }

  fun showPrintText(
    text: TextBuffer,
    showPrinter: Boolean = true,
    printerUser: List<String>,
    rota: Rota?,
    loja: Int,
    showPrintBunton: Boolean = true,
    actionSave: ((SubWindowPrinter) -> Unit)?,
    printEvent: (impressora: String) -> Unit
  ) {
    val form = SubWindowPrinter(text, showPrinter, printerUser, rota, loja, showPrintBunton, actionSave, printEvent)
    form.open()
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