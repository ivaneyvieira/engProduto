package br.com.astrosoft.produto.view.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfBarcode
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfCodigo
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfDescricao
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfEstoque
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfGrade
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfLocalizacao
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfQuantidade
import br.com.astrosoft.produto.viewmodel.pedidoTransf.TabPedidoTransfEntViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosPedTransfEnt(val viewModel: TabPedidoTransfEntViewModel, val pedido: PedidoTransf) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoPedidoTransf::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Pedido ${pedido.ordno} - ${pedido.rota}", toolBar = {
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
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)
      produtoPedidoTransfCodigo()
      produtoPedidoTransfBarcode()
      produtoPedidoTransfDescricao()
      produtoPedidoTransfGrade()
      produtoPedidoTransfLocalizacao()
      produtoPedidoTransfQuantidade()
      produtoPedidoTransfEstoque()
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoPedidoTransf> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = pedido.produtos()
    gridDetail.setItems(listProdutos)
  }
}