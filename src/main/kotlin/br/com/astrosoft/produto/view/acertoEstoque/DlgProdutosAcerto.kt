package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.AcertoEntradaProduto
import br.com.astrosoft.produto.model.beans.AcertoSaidaNota
import br.com.astrosoft.produto.model.beans.AcertoSaidaProduto
import br.com.astrosoft.produto.model.beans.PedidoAcerto
import br.com.astrosoft.produto.model.beans.ProdutoAcerto
import br.com.astrosoft.produto.viewmodel.acertoEstoque.TabAcertoEstoqueSaidaViewModel
import br.com.astrosoft.produto.viewmodel.acertoEstoque.TabAcertoPedidoViewModel
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosAcerto(val viewModel: TabAcertoPedidoViewModel, val pedido: PedidoAcerto) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoAcerto::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos do Pedido ${pedido.pedido}", toolBar = {
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
      addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS)
      isMultiSort = false

      columnGrid(ProdutoAcerto::codigo, "Código")
      columnGrid(ProdutoAcerto::barcode, "Código de Barras")
      columnGrid(ProdutoAcerto::descricao, "Descrição").expand()
      columnGrid(ProdutoAcerto::grade, "Grade")
      columnGrid(ProdutoAcerto::localizacao, "Loc App")
      columnGrid(ProdutoAcerto::validade, "val")
      columnGrid(ProdutoAcerto::qtPedido, "Quant")
      columnGrid(ProdutoAcerto::estoque, "Estoque")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoAcerto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = pedido.produtos()
    gridDetail.setItems(listProdutos)
  }
}