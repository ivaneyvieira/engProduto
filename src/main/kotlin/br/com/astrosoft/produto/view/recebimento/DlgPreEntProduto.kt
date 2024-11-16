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
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgPreEntProduto(val viewModel: TabRecebimentoPreEntViewModel, var nota: NotaEntradaXML) {
  private var chkDifRef: Checkbox? = null
  private var chkDifBar: Checkbox? = null
  private var chkDifValor: Checkbox? = null
  private var chkDifQuant: Checkbox? = null

  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNotaEntradaNdd::class.java, false)
  val numeroNota: String = nota.notaFiscal
  val loja = nota.sigla
  val pedido = nota.pedido
  val fornecedor = nota.nomeFornecedor

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

    form = SubWindowForm("Fornecedor: $fornecedor - NFO: $numeroNota", toolBar = {
      chkDifRef = checkBox("Dif Referência") {
        this.value = false
        addValueChangeListener {
          update()
        }
      }
      chkDifBar = checkBox("Dif Código Barra") {
        this.value = false
        addValueChangeListener {
          update()
        }
      }
      chkDifValor = checkBox("Dif Valor") {
        this.value = false
        addValueChangeListener {
          update()
        }
      }
      chkDifQuant = checkBox("Dif Quantidade") {
        this.value = false
        addValueChangeListener {
          update()
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
      this.addClassName("styling")
      this.format()
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false

      this.columnGroup("Pedido Compra $loja$pedido") {
        this.columnGrid({ it.produtosPedido()?.refFor }, "Referência").right()
        this.columnGrid({ it.produtosPedido()?.barcode }, "Código Barra").right()
        this.columnGrid({ it.produtosPedido()?.codigo }, "Código").right()
        this.columnGrid({ it.produtosPedido()?.descricao }, "Descrição")
        this.columnGrid({ it.produtosPedido()?.grade }, "Grade")
        this.columnGrid({ it.produtosPedido()?.quant?.format() }, "Quant", width = "60px").right()
        this.columnGrid({
          it.produtosPedido()?.valorUnit?.format("#,##0.0000")
        }, "Valor Unit", width = "100px").right()
      }

      this.columnGroup("Conversão Entrada") {
        this.columnGrid({
          val embalagem = it.produtosPedido()?.embalagem ?: return@columnGrid ""
          val quant = it.quantidade
          (quant * embalagem).format("#,##0")
        }, "Quant", width = "60px").right()
        this.columnGrid({
          val embalagem = it.produtosPedido()?.embalagem ?: return@columnGrid ""
          val valorUnit = it.valorUnitario
          (valorUnit / embalagem).format("#,##0.0000")
        }, "Valor Unit", width = "100px").right()
        this.columnGrid({ it.produtosPedido()?.embalagem?.format("#,##0") }, "Emb", width = "50px").right()
        this.columnGrid({ it.produtosPedido()?.unidade }, "Un")
      }

      this.columnGroup("XML") {
        this.columnGrid(ProdutoNotaEntradaNdd::quantidade, "Quant", width = "80px").right()
        this.columnGrid(ProdutoNotaEntradaNdd::valorUnitario, "Valor Unit", width = "100px", pattern = "#,##0.0000")
          .right()
        this.columnGrid(ProdutoNotaEntradaNdd::un, "Un")
        this.columnGrid(ProdutoNotaEntradaNdd::codigo, "Referência").right()
        this.columnGrid(ProdutoNotaEntradaNdd::codBarra, "Código Barra").right()
        this.columnGrid(ProdutoNotaEntradaNdd::cst, "CST").right()
        this.columnGrid(ProdutoNotaEntradaNdd::cfop, "CFOP").right()
        this.columnGrid(ProdutoNotaEntradaNdd::descricao, "Descrição")
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun update() {
    val listProdutos = nota.produtosNdd().filter { ndd ->
      (chkDifRef?.value != true || ndd.codigo != ndd.produtosPedido()?.refFor) &&
      (chkDifBar?.value != true || ndd.codBarra != ndd.produtosPedido()?.barcode) &&
      (chkDifValor?.value != true || ndd.valorUnitario != ndd.produtosPedido()?.valorUnit) &&
      (chkDifQuant?.value != true || ndd.quantidade.toInt() != ndd.produtosPedido()?.quant)
    }
    gridDetail.setItems(listProdutos)
  }

  fun close() {
    onClose?.invoke()
    form?.close()
  }
}