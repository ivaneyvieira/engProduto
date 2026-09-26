package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.produto.model.beans.*
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.nativeLabel
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class FormSolicitacaoCancelamento(val nota: NotaSolicitaCancelar) : FormLayout() {
  private var edtMotivo: Select<EMotivoCancelamento>? = null

  
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
  }
  
  fun solicitacaoCancelamento(): SolicitacaoCancelamento? {
    val motivo = edtMotivo?.value ?: return null
    
    return SolicitacaoCancelamento(
      motivo = motivo
    )
  }
}