package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.produto.model.beans.FornecedorClass
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaFornecedorViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.textArea
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.TextArea

class DlgEditaFornecedor(
  val viewModel: TabNotaFornecedorViewModel,
  val fornecedor: FornecedorClass,
  val onClose: () -> Unit = {}
) : Dialog() {
  var edtObs: TextArea? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    edtObs = textArea("Observações") {
      this.isAutoselect = true
      this.isAutofocus = true
      this.isClearButtonVisible = true
      this.value = fornecedor.obs ?: ""
      this.setSizeFull()
    }
    this.width = "60%"
    this.height = "60%"
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
          this@DlgEditaFornecedor.close()
        }
      }
    }
  }

  private fun headerTitle(): String {
    val vendno = fornecedor.no
    val descricao = fornecedor.descricao ?: ""

    return "$vendno - $descricao"
  }

  private fun confirmaForm() {
    fornecedor.obs = edtObs?.value ?: ""
    viewModel.saveForne(fornecedor)
    onClose.invoke()
    this.close()
  }
}