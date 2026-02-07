package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.helper.upload
import br.com.astrosoft.framework.view.vaadin.hugeRTE
import br.com.astrosoft.produto.model.beans.EmailDevolucao
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaPedidoViewModel
import com.github.mvysny.karibudsl.v10.bind
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder

class FormEmail(val viewModel: TabNotaPedidoViewModel, val email: EmailDevolucao) : VerticalLayout() {
  val binder = Binder(EmailDevolucao::class.java)
  var listAnexos: HorizontalLayout? = null

  init {
    this.width = "60%"
    this.height = "60%"
/*
    this.textField {
      this.label = "Para"
      this.isAutofocus = true
      this.setWidthFull()
      this.bind(binder).bind(EmailDevolucao::toEmail)
    }
*/
    this.textField {
      this.label = "Para"
      this.isAutofocus = true
      this.setWidthFull()
      this.bind(binder).bind(EmailDevolucao::toEmail)
    }

    horizontalLayout {
      this.isMargin = false
      this.isPadding = false
      this.setWidthFull()

      this.textField("Assunto") {
        this.setWidthFull()
        this.bind(binder).bind(EmailDevolucao::subject)
      }

      this.upload("Anexos") { fileName, dados ->
        viewModel.addAnexo(email, fileName, dados)
        updateListAnexos()
      }.apply {
        this.isDropAllowed = false
      }
    }

    listAnexos = horizontalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isWrap = true
      this.setWidthFull()
    }

    this.hugeRTE("Mensagem") {
      this.setWidthFull()
      this.setHeightFull()
      this.bind(binder).bind(EmailDevolucao::htmlContent)
    }

    updateListAnexos()
    binder.readBean(email)
  }

  fun updateListAnexos() {
    listAnexos?.removeAll()
    email.anexos.forEach { anexo ->
      val badge = Span(anexo.nomeArquivoSimples)
      badge.getElement().themeList.add("badge success")
      listAnexos?.add(badge)

      badge.onClick {
        email.removeAnexo(anexo)
        updateListAnexos()
      }
    }
  }
}
