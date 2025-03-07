package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueSaldoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField

class DlgConferencias(val viewModel: TabEstoqueSaldoViewModel, val produto: ProdutoEstoque) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private var edtConferencia: TextField? = null

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
      HorizontalLayout().apply {
        setSizeFull()
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
    viewModel.updateProduto(produto, false)
    onClose?.invoke()
    form?.close()
  }
}