package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.hugeRTE
import br.com.astrosoft.produto.model.beans.EmailDevolucao
import com.github.mvysny.karibudsl.v10.bind
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.multiSelectListBox
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.label
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.data.binder.Binder

class FormEmail(val email: EmailDevolucao) : FormLayout() {
  val binder = Binder(EmailDevolucao::class.java)

  init {
    this.width = "60%"
    this.height = "60%"
    this.setResponsiveSteps(ResponsiveStep("0", 1))
    multiSelectListBox<String> {
      this.label = "Para"
      this.bind(binder).bind(EmailDevolucao::toEmailList)
    }

    textField("Assunto") {
      this.bind(binder).bind(EmailDevolucao::subject)
    }

    button("Adicionar Anexos") {
      this.icon = VaadinIcon.PAPERCLIP.create()
    }

    hugeRTE("Mensagem") {
      this.setWidthFull()
      this.setHeightFull()
      this.bind(binder).bind(EmailDevolucao::htmlContent)
    }

    binder.readBean(email)
  }
}