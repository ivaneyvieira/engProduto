package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.helper.superDoubleField
import br.com.astrosoft.produto.model.beans.ProdutoControle
import br.com.astrosoft.produto.model.beans.ProdutoEmbalagem
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.viewmodel.estoqueCD.IModelConferencia
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabControleLojaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.miki.superfields.numbers.SuperDoubleField
import kotlin.math.roundToInt

class DlgControleSaldo(
  val viewModel: TabControleLojaViewModel,
  val produto: ProdutoControle,
  val onClose: () -> Unit = {}
) :
  Dialog() {
  private var edtDataInicial: DatePicker? = null
  private var edtConferencia: IntegerField? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      horizontalLayout {
        this.setWidthFull()
        edtDataInicial = datePicker("Início Kardec") {
          this.width = "8.5rem"
          this.value = produto.dataInicial
          this.isClearButtonVisible = true
          this.isClearButtonVisible = true
          this.localePtBr()
        }

        edtConferencia = integerField("Estoque Loja") {
          this.isAutoselect = true
          this.width = "6rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          value = produto.estoqueLoja ?: 0
          this.valueChangeMode = ValueChangeMode.LAZY
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
          closeForm()
        }
      }

      button("Cancelar") {
        this.addThemeVariants(ButtonVariant.LUMO_ERROR)
        onClick {
          this@DlgControleSaldo.close()
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

    val saldo = produto.saldo ?: 0

    return "$codigo $descricao $grade Estoque: $saldo"
  }

  private fun closeForm() {
    produto.dataInicial = edtDataInicial?.value
    produto.estoqueLoja = edtConferencia?.value
    viewModel.updateControle(produto)
    onClose.invoke()
    this.close()
  }
}