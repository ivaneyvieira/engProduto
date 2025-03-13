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
import com.vaadin.flow.component.textfield.TextField
import java.time.LocalDate

class DlgConferencias2(
  val viewModel: IModelConferencia,
  val produto: ProdutoEstoque,
  val onClose: () -> Unit = {}
) :
  Dialog() {
  private var edtConferencia: TextField? = null
  private var edtDataObservacao: DatePicker? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      horizontalLayout {
        edtDataObservacao = datePicker("Data Conf") {
          this.setWidthFull()
          this.value = LocalDate.now()
          this.localePtBr()
        }
      }
      edtConferencia = textField("Conferência") {
        this.setWidthFull()
        value = produto.observacao ?: ""
      }
    }
    this.width = "40%"
    this.height = "40%"
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
          this@DlgConferencias2.close()
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
    val dataConferencia = produto.dataObservacao.format()
    val saldo = produto.saldo ?: 0

    return "$codigo $descricao$grade ($localizacao) Data Conferência: $dataConferencia Estoque: $saldo"
  }

  private fun closeForm() {
    produto.observacao = edtConferencia?.value
    produto.dataObservacao = edtDataObservacao?.value
    viewModel.updateProduto(produto, false)
    onClose.invoke()
    this.close()
  }
}