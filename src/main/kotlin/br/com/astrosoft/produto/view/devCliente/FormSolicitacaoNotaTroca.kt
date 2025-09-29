package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.produto.model.beans.EProdutoTroca
import br.com.astrosoft.produto.model.beans.ESolicitacaoTroca
import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField

class FormSolicitacaoNotaTroca : FormLayout() {
  private var edtSolitacao: Select<ESolicitacaoTroca>? = null
  private var edtProduto: Select<EProdutoTroca>? = null
  private var edtLogin: TextField? = null
  private var edtSenha: PasswordField? = null

  init {
    edtSolitacao = select("Solicitacao") {
      this.setItems(ESolicitacaoTroca.entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = ESolicitacaoTroca.Troca
    }
    edtProduto = select("Produto") {
      this.setItems(EProdutoTroca.entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = EProdutoTroca.Com
    }
    edtLogin = textField("Login") {
      this.width = "300px"
    }
    edtSenha = passwordField("Senha") {
      this.width = "300px"
    }
  }

  val solicitacao: ESolicitacaoTroca?
    get() = edtSolitacao?.value
  val produto: EProdutoTroca?
    get() = edtProduto?.value
  val login: String
    get() = edtLogin?.value ?: ""
  val senha: String
    get() = edtSenha?.value ?: ""
}