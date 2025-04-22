package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.produto.model.beans.ProdutoEstoqueGarantia
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueGarantiaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant

class DlgConferenciaGarantia(
  val viewModel: TabEstoqueGarantiaViewModel,
  val produto: ProdutoEstoqueGarantia,
  val onClose: () -> Unit = {}
) : Dialog() {
  private var edtEstoqueReal: IntegerField? = null
  private var edtLote: TextField? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      horizontalLayout {
        edtEstoqueReal = integerField("Estoque Real") {
          this.isAutofocus = true
          this.setWidthFull()
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = produto.estoqueDev
        }
        edtLote = if (produto.temLote == true) {
          textField("Lote") {
            this.setWidthFull()
            this.isClearButtonVisible = true
            this.value = produto.loteDev
          }
        } else {
          null
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
          this@DlgConferenciaGarantia.close()
        }
      }
    }
  }

  private fun headerTitle(): String {
    val codigo = produto.codigo ?: 0
    val descricao = produto.descricao ?: ""
    val grade = produto.grade.let { gd ->
      if (gd.isNullOrBlank()) "" else " - $gd"
    }

    return "$codigo $descricao $grade"
  }

  private fun confirmaForm() {
    produto.estoqueDev = edtEstoqueReal?.value
    produto.loteDev = edtLote?.value
    viewModel.updateProduto(produto)
    onClose.invoke()
    this.close()
  }
}