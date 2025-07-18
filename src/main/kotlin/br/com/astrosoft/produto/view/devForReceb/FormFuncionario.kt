package br.com.astrosoft.produto.view.devForReceb

import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField

class FormFuncionario : FormLayout() {
  private var edtNome: TextField? = null
  private var edtSenha: PasswordField? = null

  init {
    edtNome = textField("Nome do Funcionário") {
      this.width = "300px"
    }
    edtSenha = passwordField("Senha") {
      this.width = "300px"
    }
  }

  val nome: String
    get() = edtNome?.value ?: ""
  val senha: String
    get() = edtSenha?.value ?: ""
}