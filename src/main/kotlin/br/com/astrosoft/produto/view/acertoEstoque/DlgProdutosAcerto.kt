package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.PedidoAcerto
import br.com.astrosoft.produto.model.beans.ProdutoAcerto
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.acertoEstoque.TabAcertoPedidoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosAcerto(val viewModel: TabAcertoPedidoViewModel, val pedido: PedidoAcerto) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoAcerto::class.java, false)
  private var edtPesquisa: TextField? = null

  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos do Pedido ${pedido.pedido}", toolBar = {
      edtPesquisa = textField("Pesquisa") {
        this.width = "300px"
        this.valueChangeMode = ValueChangeMode.LAZY
        this.valueChangeTimeout = 1500
        addValueChangeListener {
          update()
        }
      }

      button("Remove") {
        val user = AppConfig.userLogin() as? UserSaci
        this.isVisible = user?.acertoRemoveProd == true
        this.icon = VaadinIcon.TRASH.create()
        onClick {
          viewModel.removeProduto()
          update()
        }
      }

      button("Imprimir") {
        this.icon = VaadinIcon.PRINT.create()
        onClick {
          viewModel.previewPedido()
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
      addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS)
      isMultiSort = true
      this.selectionMode = Grid.SelectionMode.MULTI

      columnGrid(ProdutoAcerto::codigo, "Código")
      columnGrid(ProdutoAcerto::barcode, "Código de Barras")
      columnGrid(ProdutoAcerto::descricao, "Descrição")
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
    val filter = edtPesquisa?.value ?: ""
    val listProdutos = pedido.produtos().filter { produto ->
      if (filter.isBlank()) return@filter true

      if (filter in listOf("2", "3", "4", "5", "8")) {
        produto.qtPedido?.toString()?.contains(filter) == true
      } else {
        produto.pesquisaStr().contains(filter, ignoreCase = true)
      }
    }
    gridDetail.setItems(listProdutos)
  }
}