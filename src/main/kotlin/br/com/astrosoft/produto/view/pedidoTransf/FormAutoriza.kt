package br.com.astrosoft.produto.view.pedidoTransf

import br.com.astrosoft.produto.model.beans.PedidoTransf
import com.github.mvysny.karibudsl.v10.div
import com.github.mvysny.karibudsl.v10.li
import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField

class FormAutoriza(val pedido: PedidoTransf) : FormLayout() {
  private var edtLogin: TextField? = null
  private var edtSenha: PasswordField? = null

  init {
    div {
      this.li("Referente: ${pedido.referente}")
      this.li("Entregue por: ${pedido.entregue}")
      this.li("Recebido por: ${pedido.recebido}")
    }
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