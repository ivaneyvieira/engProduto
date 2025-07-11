package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoSimplesViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class DlgConferenciaAcertoSimples(
  val viewModel: TabEstoqueAcertoSimplesViewModel,
  val produto: ProdutoEstoqueAcerto,
  val onClose: () -> Unit = {}
) : Dialog() {
  private var edtDiferenca: IntegerField? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      horizontalLayout {
        edtDiferenca = integerField("Quant") {
          this.setWidthFull()
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = produto.estoqueCD
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
          this@DlgConferenciaAcertoSimples.close()
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
    produto.diferenca = edtDiferenca?.value
    viewModel.updateProduto(produto)
    onClose.invoke()
    this.close()
  }
}