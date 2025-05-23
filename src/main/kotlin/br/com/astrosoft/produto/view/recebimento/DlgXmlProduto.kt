package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.NotaEntradaXML
import br.com.astrosoft.produto.model.beans.ProdutoNotaEntradaNdd
import br.com.astrosoft.produto.viewmodel.recebimento.TabRecebimentoXmlViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class DlgXmlProduto(val viewModel: TabRecebimentoXmlViewModel, var nota: NotaEntradaXML) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNotaEntradaNdd::class.java, false)
  private var edtPesquisa: TextField? = null

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val numeroNota: String = nota.notaFiscal
    val fornecedor = nota.nomeFornecedor
    val emissao = nota.dataEmissao.format()
    val loja = nota.sigla
    val pedido = nota.pedido
    val natureza = nota.natureza ?: ""

    val linha1 = "Fornecedor: $fornecedor"
    val linha2 = "Ped Compra: $loja$pedido - NFO: $numeroNota - Emissão: $emissao"
    val linha3 = "Natureza: $natureza"

    form = SubWindowForm(
      title = "$linha1 |$linha2 |$linha3",

      toolBar = {
        edtPesquisa = textField("Pesquisa") {
          this.setWidthFull()
          this.isAutofocus = true
          this.valueChangeMode = ValueChangeMode.LAZY
          this.addValueChangeListener {
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
      columnGrid(ProdutoNotaEntradaNdd::codigo, "Código")
      columnGrid(ProdutoNotaEntradaNdd::descricao, "Descrição", width = "250px")
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
    val filtro = edtPesquisa?.value ?: ""
    val listProdutos = nota.produtosNdd().filter { prd ->
      filtro == "" ||
      prd.codigo == filtro ||
      prd.descricao.contains(filtro, ignoreCase = true) ||
      prd.codBarra == filtro ||
      prd.ncm == filtro ||
      prd.cst == filtro ||
      prd.cfop == filtro ||
      prd.un == filtro
    }
    gridDetail.setItems(listProdutos)
    gridDetail.getColumnBy(ProdutoNotaEntradaNdd::valorUnitario).setFooter("Total")
    gridDetail.getColumnBy(ProdutoNotaEntradaNdd::valorTotal).setFooter(listProdutos.sumOf { it.valorTotal }.format())
    gridDetail.getColumnBy(ProdutoNotaEntradaNdd::baseICMS).setFooter(listProdutos.sumOf { it.baseICMS }.format())
    gridDetail.getColumnBy(ProdutoNotaEntradaNdd::valorICMS).setFooter(listProdutos.sumOf { it.valorICMS }.format())
    gridDetail.getColumnBy(ProdutoNotaEntradaNdd::valorIPI).setFooter(listProdutos.sumOf { it.valorIPI }.format())
  }

  fun close() {
    onClose?.invoke()
    form?.close()
  }
}