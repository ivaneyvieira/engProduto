package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.PedidoNota
import br.com.astrosoft.produto.model.beans.PedidoProduto
import br.com.astrosoft.produto.model.beans.ValidadeSaci
import br.com.astrosoft.produto.viewmodel.recebimento.TabPedidoViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgPedidoProduto(val viewModel: TabPedidoViewModel, var pedido: PedidoNota) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(PedidoProduto::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val numeroNota: String = pedido.nfEntrada ?: ""
    val loja = pedido.loja

    form = SubWindowForm("Produtos da Nota $numeroNota Loja: $loja", toolBar = {
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
      columnGrid(PedidoProduto::codigo, "Código")
      columnGrid(PedidoProduto::descricao, "Descrição", width = "250px")
      columnGrid(PedidoProduto::grade, "Grade", width = "80px")
      columnGrid(PedidoProduto::qtty, "Qtde Pedido")
      columnGrid(PedidoProduto::qttyPendente, "Qtde Pendente")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun update() {
    val listProdutos = pedido.produtos
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