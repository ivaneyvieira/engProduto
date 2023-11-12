package br.com.astrosoft.produto.view.pedidoTransf

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ProdutoTransfRessu4
import br.com.astrosoft.produto.model.beans.TransfRessu4
import br.com.astrosoft.produto.viewmodel.pedidoTransf.TabPedidoTransfRessu4ViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosPedTransfRessu4(val viewModel: TabPedidoTransfRessu4ViewModel, val pedido: TransfRessu4) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoTransfRessu4::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Pedido ${pedido.ordno} - ${pedido.rota}", toolBar = {
      this.button("Planilha") {
        icon = VaadinIcon.FILE_TABLE.create()
        onLeftClick {
          viewModel.geraPlanilha(pedido)
        }
      }
      this.button("Relatório") {
        icon = VaadinIcon.PRINT.create()
        onLeftClick {
          viewModel.imprimeRelatorio(pedido)
        }
      }
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
      columnGrid(ProdutoTransfRessu4::codigo, "Código")
      columnGrid(ProdutoTransfRessu4::descricao, "Descrição", isExpand = true)
      columnGrid(ProdutoTransfRessu4::grade, "Grade")
      columnGrid(ProdutoTransfRessu4::codigoBarras, "Código de Barras")
      columnGrid(ProdutoTransfRessu4::referencia, "Ref Fornecedor")
      columnGrid(ProdutoTransfRessu4::quant, "Quant", pattern = "#,##0")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoTransfRessu4> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = pedido.produtos()
    gridDetail.setItems(listProdutos)
  }
}