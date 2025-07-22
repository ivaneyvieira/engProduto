package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.produto.model.beans.FornecedorClass
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaViewModel
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaFornecedorViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class DlgEditaFornecedor(
  val viewModel: TabNotaFornecedorViewModel,
  val fornecedor: FornecedorClass,
  val onClose: () -> Unit = {}
) : Dialog() {


  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()


    verticalLayout {
      setSizeFull()
      horizontalLayout {

      }
    }
    this.width = "30%"
    this.height = "30%"
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
    onClose.invoke()
    this.close()
  }
}