package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoMobileViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class DlgConferenciaAcertoMobile(
  val viewModel: TabEstoqueAcertoMobileViewModel,
  val produto: ProdutoEstoqueAcerto,
  val onClose: () -> Unit = {}
) : Dialog() {
  private var edtEstoqueCD: IntegerField? = null
  private var edtEstoqueLoja: IntegerField? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      setSizeFull()
      horizontalLayout {
        this.isMargin = false
        this.isPadding = false
        edtEstoqueCD = integerField("Estoque CD") {
          this.isAutofocus = true
          this.isAutoselect = true
          this.addClassName("mobile")
          this.addThemeVariants(TextFieldVariant.LUMO_SMALL)
          this.width = "8em"
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = produto.estoqueCD
        }

        edtEstoqueLoja = integerField("Estoque Loja") {
          this.isAutoselect = true
          this.addClassName("mobile")
          this.addThemeVariants(TextFieldVariant.LUMO_SMALL)
          this.width = "8em"
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = produto.estoqueLoja
        }
      }
    }
    this.width = "100%"
    //this.height = "30%"
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
          this@DlgConferenciaAcertoMobile.close()
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
    produto.estoqueCD = edtEstoqueCD?.value
    produto.estoqueLoja = edtEstoqueLoja?.value
    produto.updateDiferenca()
    viewModel.updateProduto(produto)
    onClose.invoke()
    this.close()
  }
}