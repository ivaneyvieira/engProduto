package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.columnGrid
import br.com.astrosoft.framework.view.vaadin.columnGroup
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.NotaEntradaXML
import br.com.astrosoft.produto.model.beans.PedidoXML
import br.com.astrosoft.produto.model.beans.ProdutoNotaEntradaNdd
import br.com.astrosoft.produto.viewmodel.recebimento.TabRecebimentoPreEntViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgPreEntProduto(val viewModel: TabRecebimentoPreEntViewModel, var nota: NotaEntradaXML) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNotaEntradaNdd::class.java, false)
  val numeroNota: String = nota.notaFiscal ?: ""
  val loja = nota.sigla
  val pedido = nota.pedido
  val produtosPedido = nota.produtosPedido()

  private fun ProdutoNotaEntradaNdd.produtosPedido(): PedidoXML? {
    val pedido = produtosPedido.firstOrNull {
      it.refFor == this.codigo
    } ?: produtosPedido.firstOrNull {
      it.barcode == this.codBarra
    }

    return pedido
  }

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose

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

      this.columnGroup("XML") {
        this.columnGrid(ProdutoNotaEntradaNdd::codigo, "Código")
        this.columnGrid(ProdutoNotaEntradaNdd::descricao, "Descrição")
        this.columnGrid(ProdutoNotaEntradaNdd::codBarra, "Cod. Barra")
        this.columnGrid(ProdutoNotaEntradaNdd::cst, "CST")
        this.columnGrid(ProdutoNotaEntradaNdd::cfop, "CFOP")
        this.columnGrid(ProdutoNotaEntradaNdd::un, "UN")
        this.columnGrid(ProdutoNotaEntradaNdd::quantidade, "Quant", width = "100px")
        this.columnGrid(ProdutoNotaEntradaNdd::valorUnitario, "Valor Unit", width = "100px", pattern = "#,##0.0000")
      }

      this.columnGroup("Conversão Entrada") {
        this.columnGrid({ it.produtosPedido()?.embalagem?.format("#,##0") }, "Emb", width = "100px").right()
        this.columnGrid({ it.produtosPedido()?.unidade }, "Un")
        this.columnGrid({
          val embalagem = it.produtosPedido()?.embalagem ?: return@columnGrid ""
          val quant = it.quantidade
          (quant * embalagem).format("#,##0")
        }, "Qtn", width = "100px").right()
        this.columnGrid({
          val embalagem = it.produtosPedido()?.embalagem ?: return@columnGrid ""
          val valorUnit = it.valorUnitario
          (valorUnit / embalagem).format("#,##0.0000")
        }, "V. Unit", width = "100px").right()
      }

      this.columnGroup("Ped Compra $pedido") {
        this.columnGrid({ it.produtosPedido()?.codigo }, "Código")
        this.columnGrid({ it.produtosPedido()?.descricao }, "Descrição")
        this.columnGrid({ it.produtosPedido()?.grade }, "Grade")
        this.columnGrid({ it.produtosPedido()?.refFor }, "Ref For")
        this.columnGrid({ it.produtosPedido()?.barcode }, "Código Barra")

        this.columnGrid({ it.produtosPedido()?.quant?.format() }, "Qtd", width = "100px").right()
        this.columnGrid({
          it.produtosPedido()?.valorUnit?.format("#,##0.0000")
        }, "V unit", width = "100px").right()
      }
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