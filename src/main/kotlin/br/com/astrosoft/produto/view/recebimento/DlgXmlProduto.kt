package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.produto.model.beans.NotaEntradaXML
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.ProdutoNotaEntradaNdd
import br.com.astrosoft.produto.viewmodel.recebimento.TabRecebimentoXmlViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgXmlProduto(val viewModel: TabRecebimentoXmlViewModel, var nota: NotaEntradaXML) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNotaEntradaNdd::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val numeroNota: String = nota.notaFiscal ?: ""
    val loja = nota.sigla
    val pedido = nota.pedido

    form = SubWindowForm("Produtos da Nota $numeroNota Loja: $loja Ped: $pedido", toolBar = {
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
      columnGrid(ProdutoNotaEntradaNdd::codigo, "Código")
      columnGrid(ProdutoNotaEntradaNdd::descricao, "Descrição")
      columnGrid(ProdutoNotaEntradaNdd::codBarra, "Cod. Barra")
      columnGrid(ProdutoNotaEntradaNdd::ncm, "NCM")
      columnGrid(ProdutoNotaEntradaNdd::cst, "CST")
      columnGrid(ProdutoNotaEntradaNdd::cfop, "CFOP")
      columnGrid(ProdutoNotaEntradaNdd::un, "UN")
      columnGrid(ProdutoNotaEntradaNdd::quantidade, "Quant")
      columnGrid(ProdutoNotaEntradaNdd::valorUnitario, "Valor Unit")
      columnGrid(ProdutoNotaEntradaNdd::valorTotal, "Valor Total")
      columnGrid(ProdutoNotaEntradaNdd::baseICMS, "Base ICMS")
      columnGrid(ProdutoNotaEntradaNdd::valorICMS, "Valor ICMS")
      columnGrid(ProdutoNotaEntradaNdd::valorIPI, "Valor IPI")
      columnGrid(ProdutoNotaEntradaNdd::aliqICMS, "Aliq ICMS")
      columnGrid(ProdutoNotaEntradaNdd::aliqIPI, "Aliq IPI")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun update() {
    val listProdutos = nota.produtosNdd()
    gridDetail.setItems(listProdutos)
  }

  fun close() {
    onClose?.invoke()
    form?.close()
  }
}