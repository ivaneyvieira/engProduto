package br.com.astrosoft.produto.view.estoqueCD

import com.github.mvysny.karibudsl.v10.div
import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.theme.lumo.LumoUtility

class FormLoginForn() : FormLayout() {
  private var edtLogin: TextField? = null
  private var edtSenha: PasswordField? = null

  init {
    edtLogin = textField("Login") {
      this.width = "300px"
    }
    edtSenha = passwordField("Senha") {
      this.width = "300px"
    }
  }

  val login: String
    get() = edtLogin?.value ?: ""
  val senha: String
    get() = edtSenha?.value ?: ""
}