package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.PedidoCapa
import br.com.astrosoft.produto.model.beans.PedidoNota
import br.com.astrosoft.produto.model.beans.ValidadeSaci
import br.com.astrosoft.produto.viewmodel.recebimento.TabPedidoViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgNotaPedido(val viewModel: TabPedidoViewModel, var pedido: PedidoCapa) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(PedidoNota::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val numeroNota: Int = pedido.pedido
    val loja = pedido.loja

    form = SubWindowForm("Notas do pedido $numeroNota Loja: $loja",
      toolBar = { }, onClose = {
        onClose()
      }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form?.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      this.format()
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      columnGrid(PedidoNota::loja, "Loja")
      addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
        val dlgProduto = DlgProdutosPedido(viewModel, pedido)
        dlgProduto.showDialog {
          viewModel.updateView()
        }
      }
      columnGrid(PedidoNota::dataEmissao, "Emissão")
      columnGrid(PedidoNota::nfEntrada, "NF", width = "100px").right()
      columnGrid(PedidoNota::dataEntrada, "Entrada")
      columnGrid(PedidoNota::invno, "NI")
      columnGrid(PedidoNota::tipoNota, "Tipo")
      columnGrid(PedidoNota::totalProduto, "Total Pedido")
      columnGrid(PedidoNota::totalProdutoPendente, "Total Pendente")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun update() {
    val listNotas = pedido.notas
    gridDetail.setItems(listNotas)
  }

  fun close() {
    onClose?.invoke()
    form?.close()
  }

  fun openValidade(tipoValidade: Int, tempoValidade: Int, block: (ValidadeSaci) -> Unit) {
    val form = FormValidade(tipoValidade, tempoValidade)
    DialogHelper.showForm(caption = "Validade", form = form) {
      block(form.validadeSaci)
    }
  }

  fun reloadGrid() {
    gridDetail.dataProvider.refreshAll()
  }
}