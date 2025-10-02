package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.produto.model.beans.EMotivoTroca
import br.com.astrosoft.produto.model.beans.FornecedorClass
import br.com.astrosoft.produto.model.beans.NotaVenda
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevAutorizaViewModel
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaFornecedorViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.checkBoxGroup
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.textArea
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.TextArea

class DlgEditaMotivo(
  val viewModel: TabDevAutorizaViewModel,
  val nota: NotaVenda,
  val onClose: () -> Unit = {}
) : Dialog() {
  var edtMotivo: CheckboxGroup<EMotivoTroca>? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    edtMotivo = checkBoxGroup("Motivo:") {
      this.setItems(EMotivoTroca.entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.value = nota.setMotivoTroca
      this.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL)
      this.isReadOnly = false
      this.width = "100%"
    }
    this.width = "18rem"
    this.height = "22rem"
  }

  fun HasComponents.toolBar() {
    horizontalLayout {
      this.justifyContentMode = FlexComponent.JustifyContentMode.END
      button("Confirma") {
        this.setPrimary()
        onClick {
          confirmaForm()
        }
      }

      button("Cancelar") {
        this.addThemeVariants(ButtonVariant.LUMO_ERROR)
        onClick {
          this@DlgEditaMotivo.close()
        }
      }
    }
  }

  private fun headerTitle(): String {
    val loja = nota.loja
    val nota = nota.nota ?: ""

    return "$loja - $nota"
  }

  private fun confirmaForm() {
    nota.setMotivoTroca = edtMotivo?.value.orEmpty()
    viewModel.saveNota(nota)
    onClose.invoke()
    this.close()
  }
}