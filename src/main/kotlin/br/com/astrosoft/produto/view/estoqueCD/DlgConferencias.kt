package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueSaldoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import java.time.LocalDate

class DlgConferencias(val viewModel: TabEstoqueSaldoViewModel, val produto: ProdutoEstoque) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private var edtConferencia: TextField? = null
  private var edtDataInicial: DatePicker? = null
  private var edtDataObservacao: DatePicker? = null

  fun showDialog(onClose: () -> Unit = {}) {
    this.onClose = onClose
    val codigo = produto.codigo ?: 0
    val descricao = produto.descricao ?: ""
    val grade = produto.grade.let { gd ->
      if (gd.isNullOrBlank()) "" else " - $gd"
    }

    val localizacao = produto.locApp
    val dataConferencia = produto.dataObservacao

    form = SubWindowForm(
      "$codigo $descricao$grade ($localizacao) Data Conferência: ${dataConferencia.format()} Estoque: ${produto.saldo ?: 0}",
      toolBar = {
        button("Cancelar") {
          addClickListener {
            form?.close()
          }
        }
      },
      onClose = {
        closeForm()
      }) {
      VerticalLayout().apply {
        setSizeFull()
        horizontalLayout {
          edtDataInicial = datePicker("Data Inicial Kardex") {
            this.setWidthFull()
            this.value = produto.dataInicial
            this.isClearButtonVisible = true
            this.isClearButtonVisible = true
            this.localePtBr()
          }
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
    }
    form?.width = "40%"
    form?.height = "40%"
    form?.open()
  }

  private fun closeForm() {
    produto.observacao = edtConferencia?.value
    produto.dataObservacao = edtDataObservacao?.value
    produto.dataInicial = edtDataInicial?.value
    viewModel.updateProduto(produto, false)
    onClose?.invoke()
    form?.close()
  }
}