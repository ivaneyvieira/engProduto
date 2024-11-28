package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.columnGrid
import br.com.astrosoft.framework.view.vaadin.columnGroup
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper.showWarning
import br.com.astrosoft.produto.model.beans.NotaEntradaXML
import br.com.astrosoft.produto.model.beans.PedidoXML
import br.com.astrosoft.produto.model.beans.ProdutoNotaEntradaNdd
import br.com.astrosoft.produto.view.recebimento.EDiferenca.*
import br.com.astrosoft.produto.viewmodel.recebimento.TabRecebimentoPreEntViewModel
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select

class DlgPreEntProduto(val viewModel: TabRecebimentoPreEntViewModel, var nota: NotaEntradaXML) {
  private var cmbDiferenca: Select<EDiferenca>? = null

  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNotaEntradaNdd::class.java, false)
  private val numeroNota: String = nota.notaFiscal
  private val loja = nota.sigla
  private val pedido = nota.pedido
  private val fornecedor = nota.nomeFornecedor

  private val produtosPedido = nota.produtosPedido()

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

      this.withEditor(
        ProdutoNotaEntradaNdd::class,
        openEditor = {
          this.focusEditor(ProdutoNotaEntradaNdd::quantFatPedido)
        },
        closeEditor = {
          viewModel.salvaItemPedido(it.bean)
        },
        canEdit = { bean ->
          if (bean?.difQtdPedido == true) {
            true
          } else {
            showWarning("Não é possível editar este item")
            false
          }
        })

      this.columnGroup("Pedido Compra $loja$pedido") {
        this.columnGrid(ProdutoNotaEntradaNdd::refForPedido, "Referência").right().apply {
          this.setPartNameGenerator {
            if (it.difRefPedido) "amarelo" else null
          }
        }
        this.columnGrid(ProdutoNotaEntradaNdd::barcodePedido, "Código Barra").right().apply {
          this.setPartNameGenerator {
            if (it.difBarPedido) "amarelo" else null
          }
        }
        this.columnGrid(ProdutoNotaEntradaNdd::codigoPedido, "Código").right()
        this.columnGrid(ProdutoNotaEntradaNdd::descricaoPedido, "Descrição")
        this.columnGrid(ProdutoNotaEntradaNdd::gradePedido, "Grade")
        this.columnGrid(ProdutoNotaEntradaNdd::quantPedido, "Quant", width = "100px")
        this.columnGrid(ProdutoNotaEntradaNdd::quantFatPedido, "Quant Fat", width = "100px") {
          this.integerFieldEditor()
          this.setPartNameGenerator() {
            if (it.difQtdPedido) "amarelo" else null
          }
        }
        this.columnGrid(ProdutoNotaEntradaNdd::valorUnitPedido, "Valor Unit", pattern = "#,##0.0000", width = "100px") {
          this.setPartNameGenerator {
            if (it.difValPedido) "amarelo" else null
          }
        }
      }

      this.columnGroup("Conversão Entrada") {
        this.columnGrid(ProdutoNotaEntradaNdd::quantConvPedido, "Quant", pattern = "#,##0.0000", width = "80px") {
          this.setPartNameGenerator() {
            if (it.difQtdPedido) "amarelo" else null
          }
        }
        this.columnGrid(ProdutoNotaEntradaNdd::valorConvPedido, "Valor Unit", pattern = "#,##0.0000", width = "100px") {
          this.setPartNameGenerator() {
            if (it.difValPedido) "amarelo" else null
          }
        }
        this.columnGrid(ProdutoNotaEntradaNdd::embalagemFatorPedido, "Emb", pattern = "#,##0.0000", width = "80px")

        this.columnGrid(ProdutoNotaEntradaNdd::unidadePedido, "Un")
      }

      this.columnGroup("XML") {
        this.columnGrid(ProdutoNotaEntradaNdd::quantidade, "Quant", width = "80px")
        this.columnGrid(ProdutoNotaEntradaNdd::valorUnitario, "Valor Unit", width = "100px", pattern = "#,##0.0000")
        this.columnGrid(ProdutoNotaEntradaNdd::un, "Un")
        this.columnGrid(ProdutoNotaEntradaNdd::codigo, "Referência") {
          this.right()
          this.setPartNameGenerator() {
            if (it.difRefPedido) "amarelo" else null
          }
        }
        this.columnGrid(ProdutoNotaEntradaNdd::codBarra, "Código Barra").right().apply {
          this.setPartNameGenerator() {
            if (it.difBarPedido) "amarelo" else null
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

  fun update() {
    val listProdutos = nota.produtosNdd().map { ndd ->
      ndd.pedidoXML = ndd.produtosPedido()
      ndd
    }.filter { ndd ->
      val value = cmbDiferenca?.value ?: TODOS
      (value == REF && ndd.difRefPedido) ||
      (value == BAR && ndd.difBarPedido) ||
      (value == VAL && ndd.difValPedido) ||
      (value == QTD && ndd.difQtdPedido) ||
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