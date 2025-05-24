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

class DlgConferenciaProduto(
  val viewModel: ITabNotaViewModel,
  val produto: NotaRecebimentoProdutoDev,
  val onClose: () -> Unit = {}
) : Dialog() {
  private var edtEstoqueReal: IntegerField? = null
  private var edtNI: IntegerField? = null
  private var edtGrade: Select<String>? = null

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
        edtNI = integerField("NI") {
          this.width  = "120px"
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = produto.ni
        }

        if (listaGrades.isNotEmpty()) {
          edtGrade = select("Grade") {
            this.width  = "120px"
            this.setItems(listaGrades)
            this.value = listaGrades.firstOrNull { it == produto.grade }
          }
        }

        edtEstoqueReal = integerField("Qntd") {
          this.width = "120px"
          this.isAutofocus = true
          this.isClearButtonVisible = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = produto.quantDevolucao
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
          this@DlgConferenciaProduto.close()
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
    produto.quantDevolucao = edtEstoqueReal?.value
    val grade = edtGrade?.value
    val ni = edtNI?.value
    viewModel.updateProduto(produto = produto, grade = grade ?: produto.grade, ni = ni ?: produto.ni)
    onClose.invoke()
    this.close()
  }
}