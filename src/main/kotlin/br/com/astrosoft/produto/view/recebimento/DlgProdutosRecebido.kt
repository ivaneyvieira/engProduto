package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.NotaRecebimento
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto
import br.com.astrosoft.produto.viewmodel.recebimento.TabRecebidoViewModel
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosRecebido(val viewModel: TabRecebidoViewModel, val nota: NotaRecebimento) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaRecebimentoProduto::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val numeroNota = nota.nfEntrada ?: ""

    form = SubWindowForm("Produtos da nota $numeroNota", toolBar = {
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
      setSelectionMode(Grid.SelectionMode.MULTI)

      columnGrid(NotaRecebimentoProduto::codigo, "Código")
      columnGrid(NotaRecebimentoProduto::barcodeStrList, "Código de Barras")
      columnGrid(NotaRecebimentoProduto::descricao, "Descrição")
      columnGrid(NotaRecebimentoProduto::grade, "Grade")
      columnGrid(NotaRecebimentoProduto::localizacao, "Loc App")
      columnGrid(NotaRecebimentoProduto::quant, "Quant")
      columnGrid(NotaRecebimentoProduto::estoque, "Estoque")
      columnGrid(NotaRecebimentoProduto::validade, "Val", width = "100px")
      columnGrid(NotaRecebimentoProduto::fabricacao, "Fab", width = "120px", pattern = "MM/yy")
      columnGrid(NotaRecebimentoProduto::vencimento, "Venc", width = "120px", pattern = "MM/yy")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<NotaRecebimentoProduto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): NotaRecebimentoProduto? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { prd ->
      prd.containBarcode(codigoBarra)
    }
  }
}