package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.produto.model.beans.FornecedorClass
import br.com.astrosoft.produto.model.beans.NotaVenda
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevAutorizaViewModel
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

class DlgEditaMotivo(
  val viewModel: TabDevAutorizaViewModel,
  val nota: NotaVenda,
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
      this.value = nota.motivoTroca ?: ""
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
    nota.motivoTroca = edtObs?.value ?: ""
    viewModel.saveNota(nota)
    onClose.invoke()
    this.close()
  }
}