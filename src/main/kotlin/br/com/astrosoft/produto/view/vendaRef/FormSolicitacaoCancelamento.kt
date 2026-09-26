package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.produto.model.beans.*
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.nativeLabel
import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant

class FormSolicitacaoCancelamento(val nota: NotaSolicitaCancelar) : FormLayout() {
  private var edtMotivo: Select<EMotivoCancelamento>? = null
  private var edtLogin: TextField? = null
  private var edtSenha: PasswordField? = null
  
  init {
    val readOnly = !nota.loginCancel.isNullOrBlank()
    val user = AppConfig.userLogin() as? UserSaci
    edtMotivo = select("Motivo do Cancelamento") {
      this.isReadOnly = readOnly
      val tipos = EMotivoCancelamento.entries
      this.setItems(tipos)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = nota.motivoEnum
    }
    edtLogin = textField("Login") {
      this.width = "300px"
    }
    edtSenha = passwordField("Senha") {
      this.width = "300px"
    }
  }
  
  fun solicitacaoCancelamento(): SolicitacaoCancelamento? {
    val motivo = edtMotivo?.value ?: return null
    val login = edtLogin?.value ?: return null
    val senha = edtSenha?.value ?: return null
    
    return SolicitacaoCancelamento(
      login = login,
      senha = senha,
      motivo = motivo,
    )
  }
}