package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class DlgAcertoProduto(
  val viewModel: ITabNotaViewModel,
  val produtoSelecionado: List<NotaRecebimentoProdutoDev>,
  val onClose: () -> Unit = {}
) : Dialog() {
  private var edtAcerto: IntegerField? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      horizontalLayout {
        edtAcerto = integerField("Acerto") {
          this.isAutofocus = true
          this.width = "10rem"
          this.isAutoselect = true
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = produtoSelecionado.firstOrNull { (it.numAcerto ?: 0) > 0 }?.numAcerto ?: 0
        }
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
          this@DlgAcertoProduto.close()
        }
      }
    }
  }

  private fun headerTitle(): String {
    return "Acerto de Produtos"
  }

  private fun confirmaForm() {
    produtoSelecionado.forEach { produto ->
      produto.numAcerto = edtAcerto?.value ?: 0
      viewModel.updateAcertoProduto(produto = produto)
    }
    onClose.invoke()
    this.close()
  }
}