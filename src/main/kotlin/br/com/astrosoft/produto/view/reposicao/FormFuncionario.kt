package br.com.astrosoft.produto.view.reposicao

import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.passwordField
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.PasswordField

class FormFuncionario : FormLayout() {
  private var edtNumero: IntegerField? = null
  private var edtSenha: PasswordField? = null

  init {
    edtNumero = integerField("Número do Funcionário") {
      this.width = "300px"
    }
    edtSenha = passwordField("Senha") {
      this.width = "300px"
    }
  }

  val numero: Int
    get() = edtNumero?.value ?: 0
  val senha: String
    get() = edtSenha?.value ?: ""
}