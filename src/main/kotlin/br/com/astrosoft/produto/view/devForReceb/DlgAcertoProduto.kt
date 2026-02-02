package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class DlgAcertoProduto(
  val viewModel: ITabNotaViewModel,
  val produto: NotaRecebimentoProdutoDev,
  val onClose: () -> Unit = {}
) : Dialog() {
  private var edtAcerto: IntegerField? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    val listaGrades = produto.codigo?.toString()?.let {
      viewModel.findProdutos(it)
    }.orEmpty().map {
      it.grade
    }.filter {
      it.isNotBlank()
    }.distinct()

    verticalLayout {
      setSizeFull()
      horizontalLayout {
        edtAcerto = integerField("Acerto") {
          this.width = "10rem"
          this.isAutoselect = true
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = produto.numAcerto
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
    val codigo = produto.codigo ?: 0
    val descricao = produto.descricao ?: ""
    val grade = produto.grade.let { gd ->
      if (gd.isNullOrBlank()) "" else " - $gd"
    }

    return "$codigo $descricao $grade"
  }

  private fun confirmaForm() {
    produto.numAcerto = edtAcerto?.value ?: 0
    viewModel.updateAcertoProduto(produto = produto)
    onClose.invoke()
    this.close()
  }
}