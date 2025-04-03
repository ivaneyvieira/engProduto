package br.com.astrosoft.produto.view.devFor2

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.NotaRecebimento
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto
import br.com.astrosoft.produto.viewmodel.devFor2.TabNotaPendenciaViewModel
import com.github.mvysny.karibudsl.v10.bigDecimalField
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.BigDecimalField
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosNotaPendencia(val viewModel: TabNotaPendenciaViewModel, val nota: NotaRecebimento) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaRecebimentoProduto::class.java, false)
  private var edtVolume: IntegerField? = null
  private var edtTrans: IntegerField? = null
  private var edtPeso: BigDecimalField? = null
  private var edtTransportadora: TextField? = null
  private var edtCte: TextField? = null

  fun showDialog(onClose: () -> Unit) {
    val numeroNota = nota.nfEntrada ?: ""
    val fornecedor = nota.fornecedor ?: ""
    val emissao = nota.emissao.format()
    val loja = nota.lojaSigla ?: ""
    val pedido = nota.pedComp?.toString() ?: ""
    val numeroInterno = nota.ni
    val transp = nota.transp
    val transportadora = nota.transportadora
    val tipoDevolucao = nota.tipoDevolucaoName ?: ""
    val cte = nota.cte

    val linha1 = "Fornecedor: $fornecedor"
    val linha2 = "NI: $numeroInterno - Nota: $numeroNota - Emissão: $emissao - Ped Compra: $loja$pedido"
    val linha3 = "Transportadora: $transp - $transportadora     CTE: $cte"
    val linha4 = "Motivo Devolução: $tipoDevolucao"

    form = SubWindowForm(
      title = "$linha1|$linha2|$linha3|$linha4",
      toolBar = {
        edtVolume = integerField("Volume") {
          this.value = nota.volumeDevolucao
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.width = "6rem"
          this.isAutoselect = true
          this.valueChangeMode = ValueChangeMode.LAZY

          addValueChangeListener {
            nota.volumeDevolucao = this.value
            viewModel.saveNota(nota)
          }
        }
        edtPeso = bigDecimalField("Peso") {
          this.value = nota.pesoDevolucao.toBigDecimal()
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.width = "6rem"
          this.isAutoselect = true
          this.valueChangeMode = ValueChangeMode.LAZY

          addValueChangeListener {
            nota.pesoDevolucao = this.value.toDouble()
            viewModel.saveNota(nota)
          }
        }
        edtTrans = integerField("Cod") {
          this.value = nota.transpDevolucao
          this.width = "60px"
          this.isAutoselect = true
          this.valueChangeMode = ValueChangeMode.LAZY

          addValueChangeListener {
            nota.transpDevolucao = this.value
            viewModel.saveNota(nota)
            edtTransportadora?.value = viewModel.findTransportadora(edtTrans?.value)?.nome ?: ""
          }
        }
        edtTransportadora = textField("Transportadora Redespacho") {
          this.isReadOnly = true
          this.width = "300px"
          this.value = viewModel.findTransportadora(nota.transpDevolucao)?.nome ?: ""
        }
        edtCte = textField("CTE") {
          this.isReadOnly = true
          this.width = "60px"
          this.value = nota.cteDevolucao
          this.valueChangeMode = ValueChangeMode.LAZY

          addValueChangeListener {
            nota.cteDevolucao = this.value
            viewModel.saveNota(nota)
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
      removeThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI

      columnGrid(NotaRecebimentoProduto::codigo, "Código").right()
      columnGrid(NotaRecebimentoProduto::barcodeStrListEntrada, "Código de Barras").right()
      columnGrid(NotaRecebimentoProduto::refFabrica, "Ref Fabrica").right()
      columnGrid(NotaRecebimentoProduto::descricao, "Descrição")
      columnGrid(NotaRecebimentoProduto::grade, "Grade", width = "80px")
      columnGrid(NotaRecebimentoProduto::cfop, "CFOP")
      columnGrid(NotaRecebimentoProduto::cst, "CST")
      columnGrid(NotaRecebimentoProduto::un, "UN")
      columnGrid(NotaRecebimentoProduto::quantDevolucao, "Quant")
      columnGrid(NotaRecebimentoProduto::valorUnit, "Valor Unit", pattern = "#,##0.0000", width = "90px")
      columnGrid(NotaRecebimentoProduto::valorTotalDevolucao, "Valor Total", width = "90px")
      columnGrid(NotaRecebimentoProduto::valorDescontoDevolucao, "Desc", width = "60px")
      columnGrid(NotaRecebimentoProduto::freteDevolucao, "Frete", width = "60px")
      columnGrid(NotaRecebimentoProduto::outDespDevolucao, "Desp", width = "60px")
      columnGrid(NotaRecebimentoProduto::baseIcmsDevolucao, "Base ICMS", width = "90px")
      columnGrid(NotaRecebimentoProduto::icmsSubstDevolucao, "Valor ST", width = "90px")
      columnGrid(NotaRecebimentoProduto::valIcmsDevolucao, "V. ICMS", width = "70px")
      columnGrid(NotaRecebimentoProduto::valIPIDevolucao, "V. IPI", width = "60px")
      columnGrid(NotaRecebimentoProduto::icms, "ICMS", width = "60px")
      columnGrid(NotaRecebimentoProduto::ipi, "IPI", width = "50px")
      columnGrid(NotaRecebimentoProduto::totalGeralDevolucao, "Total", width = "90px")
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
    gridDetail.getColumnBy(NotaRecebimentoProduto::valorTotalDevolucao).setFooter(
      listProdutos.sumOf { it.valorTotalDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProduto::valorDescontoDevolucao).setFooter(
      listProdutos.sumOf { it.valorDescontoDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProduto::freteDevolucao).setFooter(
      listProdutos.sumOf { it.freteDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProduto::outDespDevolucao).setFooter(
      listProdutos.sumOf { it.outDespDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProduto::baseIcmsDevolucao).setFooter(
      listProdutos.sumOf { it.baseIcmsDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProduto::icmsSubstDevolucao).setFooter(
      listProdutos.sumOf { it.icmsSubstDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProduto::valIcmsDevolucao).setFooter(
      listProdutos.sumOf { it.valIcmsDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProduto::valIPIDevolucao).setFooter(
      listProdutos.sumOf { it.valIPIDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProduto::totalGeralDevolucao).setFooter(
      listProdutos.sumOf { it.totalGeralDevolucao ?: 0.0 }.format("#,##0.00")
    )
  }

  fun produtosCodigoBarras(codigoBarra: String): NotaRecebimentoProduto? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { prd ->
      prd.containBarcode(codigoBarra)
    }
  }

  fun updateProduto(): NotaRecebimento? {
    val nota = nota.refreshProdutos(true)
    update()
    return nota
  }
}