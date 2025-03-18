package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.viewmodel.estoqueCD.IModelConferencia
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant
import java.time.LocalDate

class DlgConferenciaConf(
  val viewModel: IModelConferencia,
  val produto: ProdutoEstoque,
  val onClose: () -> Unit = {}
) :
  Dialog() {
  private var edtEstoqueCD: IntegerField? = null
  private var edtEstoqueLoja: IntegerField? = null
  private var edtEstoqueData: DatePicker? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      horizontalLayout {
        edtEstoqueData = datePicker("Data Conf") {
          this.setWidthFull()
          this.value = LocalDate.now()
          this.localePtBr()
        }
      }

      horizontalLayout {
        edtEstoqueCD = integerField("Estoque CD") {
          this.setWidthFull()
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = produto.estoqueCD
        }

        edtEstoqueLoja = integerField("Estoque Loja") {
          this.setWidthFull()
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = produto.estoqueLoja
        }
      }
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
          closeForm()
        }
      }

      button("Cancelar") {
        this.addThemeVariants(ButtonVariant.LUMO_ERROR)
        onClick {
          this@DlgConferenciaConf.close()
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

    val localizacao = produto.locApp
    val dataConferencia = produto.estoqueData.format()
    val saldo = produto.saldo ?: 0

    return "$codigo $descricao$grade ($localizacao) Data Conferência: $dataConferencia Estoque: $saldo"
  }

  private fun closeForm() {
    produto.estoqueCD = edtEstoqueCD?.value
    produto.estoqueLoja = edtEstoqueLoja?.value
    produto.estoqueData = edtEstoqueData?.value
    viewModel.updateProduto(produto, false)
    onClose.invoke()
    this.close()
  }
}