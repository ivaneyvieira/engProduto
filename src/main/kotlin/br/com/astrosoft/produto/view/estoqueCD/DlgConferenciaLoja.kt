package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.viewmodel.estoqueCD.IModelConferencia
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class DlgConferenciaLoja(
  val viewModel: IModelConferencia,
  val produto: ProdutoEstoque,
  val onClose: () -> Unit = {}
) :
  Dialog() {
  private var edtDataConf: DatePicker? = null
  private var edtEditCD: IntegerField? = null
  private var edtEditLoja: IntegerField? = null

  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      horizontalLayout {
        this.setWidthFull()
        edtDataConf = datePicker("Conferencia") {
          this.width = "8.5rem"
          this.value = produto.dataConferencia
          this.isClearButtonVisible = true
          this.isClearButtonVisible = true
          this.localePtBr()
        }

        edtEditCD = integerField("Est CD") {
          this.isAutoselect = true
          this.width = "6rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          value = produto.qtConfEdit ?: 0

          addKeyUpListener(Key.ENTER, {
            edtEditLoja?.focus()
          })
        }

        edtEditLoja = integerField("Est Loja") {
          this.isAutoselect = true
          this.width = "6rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          value = produto.qtConfEditLoja

          addKeyUpListener(Key.ENTER, {
            edtDataConf?.focus()
          })
        }

        edtDataConf?.focus()
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
          this@DlgConferenciaLoja.close()
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
    //val dataConferencia = produto.dataConferencia.format()
    val saldo = produto.saldo ?: 0

    return "$codigo $descricao$grade ($localizacao) Estoque: $saldo"
  }

  private fun closeForm() {
    produto.dataConferencia = edtDataConf?.value
    produto.qtConfEdit = edtEditCD?.value
    produto.qtConfEditLoja = edtEditLoja?.value
    viewModel.updateConferencia(produto)
    onClose.invoke()
    this.close()
  }
}