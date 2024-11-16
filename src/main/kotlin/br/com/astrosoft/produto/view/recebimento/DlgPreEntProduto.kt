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
import br.com.astrosoft.produto.view.recebimento.EDiferenca.*
import br.com.astrosoft.produto.viewmodel.recebimento.TabRecebimentoPreEntViewModel
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select

class DlgPreEntProduto(val viewModel: TabRecebimentoPreEntViewModel, var nota: NotaEntradaXML) {
  private var cmbDiferenca: Select<EDiferenca>? = null

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

  private fun ProdutoNotaEntradaNdd.codigo(): Int? {
    return produtosPedido()?.codigo
  }

  private fun ProdutoNotaEntradaNdd.refFor(): String? {
    return produtosPedido()?.refFor
  }

  private fun ProdutoNotaEntradaNdd.barcode(): String? {
    return produtosPedido()?.barcode
  }

  private fun ProdutoNotaEntradaNdd.descricao(): String? {
    return produtosPedido()?.descricao
  }

  private fun ProdutoNotaEntradaNdd.grade(): String? {
    return produtosPedido()?.grade
  }

  private fun ProdutoNotaEntradaNdd.quant(): Int? {
    return produtosPedido()?.quant
  }

  private fun ProdutoNotaEntradaNdd.valorUnit(): Double? {
    return produtosPedido()?.valorUnit
  }

  private fun ProdutoNotaEntradaNdd.fator(): Double? {
    return produtosPedido()?.fator
  }

  private fun ProdutoNotaEntradaNdd.quantConv(): Double? {
    val embalagem = fator() ?: return null
    val quant = quantidade
    return (quant * embalagem)
  }

  private fun ProdutoNotaEntradaNdd.valorConv(): Double? {
    val embalagem = fator() ?: return null
    val valorUnit = valorUnitario
    return (valorUnit / embalagem)
  }

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose

    form = SubWindowForm("Fornecedor: $fornecedor - NFO: $numeroNota", toolBar = {
      cmbDiferenca = select("Diferença") {
        setItems(EDiferenca.entries)
        this.value = TODOS
        setItemLabelGenerator {
          it.descricao
        }
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
        this.columnGrid({ it.refFor() }, "Referência").right().apply {
          this.setPartNameGenerator() {
            if (it.difRef()) "amarelo" else null
          }
        }
        this.columnGrid({ it.barcode() }, "Código Barra").right().apply {
          this.setPartNameGenerator() {
            if (it.difBar()) "amarelo" else null
          }
        }
        this.columnGrid({ it.codigo() }, "Código").right()
        this.columnGrid({ it.descricao() }, "Descrição")
        this.columnGrid({ it.grade() }, "Grade")
        this.columnGrid({ it.quant()?.format() }, "Quant", width = "60px").right().apply {
          this.setPartNameGenerator() {
            if (it.difQtd()) "amarelo" else null
          }
        }
        this.columnGrid({ it.valorUnit()?.format("#,##0.0000") }, "Valor Unit", width = "100px").right().apply {
          this.setPartNameGenerator() {
            if (it.difVal()) "amarelo" else null
          }
        }
      }

      this.columnGroup("Conversão Entrada") {
        this.columnGrid({ it.quantConv()?.format("#,##0.0000") }, "Quant", width = "80px").right().apply {
          this.setPartNameGenerator() {
            if (it.difQtd()) "amarelo" else null
          }
        }
        this.columnGrid({ it.valorConv()?.format("#,##0.0000") }, "Valor Unit", width = "100px").right().apply {
          this.setPartNameGenerator() {
            if (it.difVal()) "amarelo" else null
          }
        }
        this.columnGrid({ it.produtosPedido()?.embalagemFator?.format("#,##0.0000") }, "Emb", width = "80px").right()
        this.columnGrid({ it.produtosPedido()?.unidade }, "Un")
      }


      this.columnGroup("XML") {
        this.columnGrid(ProdutoNotaEntradaNdd::quantidade, "Quant", width = "80px").right()
        this.columnGrid(ProdutoNotaEntradaNdd::valorUnitario, "Valor Unit", width = "100px", pattern = "#,##0.0000")
          .right()
        this.columnGrid(ProdutoNotaEntradaNdd::un, "Un")
        this.columnGrid(ProdutoNotaEntradaNdd::codigo, "Referência").right().apply {
          this.setPartNameGenerator() {
            if (it.difRef()) "amarelo" else null
          }
        }
        this.columnGrid(ProdutoNotaEntradaNdd::codBarra, "Código Barra").right().apply {
          this.setPartNameGenerator() {
            if (it.difBar()) "amarelo" else null
          }
        }
        this.columnGrid(ProdutoNotaEntradaNdd::cst, "CST").right()
        this.columnGrid(ProdutoNotaEntradaNdd::cfop, "CFOP").right()
        this.columnGrid(ProdutoNotaEntradaNdd::descricao, "Descrição")
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun ProdutoNotaEntradaNdd.difRef(): Boolean {
    return this.codigo != this.refFor()
  }

  fun ProdutoNotaEntradaNdd.difBar(): Boolean {
    return this.barcode() != this.codBarra
  }

  fun ProdutoNotaEntradaNdd.difQtd(): Boolean {
    return this.quantConv()?.toInt() != this.quant()
  }

  fun ProdutoNotaEntradaNdd.difVal(): Boolean {
    return this.valorConv()?.format("0.0000") != this.valorUnit()?.format("0.0000")
  }

  fun update() {
    val listProdutos = nota.produtosNdd().filter { ndd ->
      val value = cmbDiferenca?.value ?: TODOS
      (value == REF && ndd.difRef()) ||
      (value == BAR && ndd.difBar()) ||
      (value == VAL && ndd.difVal()) ||
      (value == QTD && ndd.difQtd()) ||
      (value == TODOS)
    }
    gridDetail.setItems(listProdutos)
  }

  fun close() {
    onClose?.invoke()
    form?.close()
  }
}

enum class EDiferenca(val descricao: String) {
  TODOS("Todos"),
  BAR("Código de Barras"),
  REF("Referência"),
  QTD("Quantidade"),
  VAL("Valor"),
}