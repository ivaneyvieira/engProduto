package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.recebimento.TabPedidoViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgPedidoProdutoCompra(val viewModel: TabPedidoViewModel, var pedido: PedidoCapa) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(PedidoProdutoCompra::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val numero: Int = pedido.pedido
    val loja = pedido.loja

    form = SubWindowForm("Produtos do Pedido $numero Loja: $loja", toolBar = {
    }, onClose = {
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
      columnGrid(PedidoProdutoCompra::codigo, "Código")
      columnGrid(PedidoProdutoCompra::descricao, "Descrição")
      columnGrid(PedidoProdutoCompra::grade, "Grade")
      columnGrid(PedidoProdutoCompra::qttyPedido, "Qtde Pedido")
      columnGrid(PedidoProdutoCompra::qttyPendente, "Qtde Pendente")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun update() {
    val listProdutos = pedido.produtosCompra()
    gridDetail.setItems(listProdutos)
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