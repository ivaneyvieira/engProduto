package br.com.astrosoft.produto.view.devFor2

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.NotaRecebimento
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto
import br.com.astrosoft.produto.viewmodel.devFor2.TabNotaTransportadoraViewModel
import com.github.mvysny.karibudsl.v10.bigDecimalField
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextFieldVariant

class DlgProdutosNotaTransportadora(val viewModel: TabNotaTransportadoraViewModel, val nota: NotaRecebimento) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaRecebimentoProduto::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    val numeroNota = nota.nfEntrada ?: ""
    val fornecedor = nota.fornecedor ?: ""
    val emissao = nota.emissao.format()
    val loja = nota.lojaSigla ?: ""
    val pedido = nota.pedComp?.toString() ?: ""
    val numeroInterno = nota.ni
    val transp = nota.transp
    val transportadora = nota.transportadora
    val tipoDevolucao = nota.tipoDevolucaoName?.uppercase() ?: ""
    val cte = nota.cte

    val linha1 = "Fornecedor: $fornecedor"
    val linha2 = "NI: $numeroInterno - Nota: $numeroNota - Emissão: $emissao - Ped Compra: $loja$pedido"
    val linha3 = "Transportadora: $transp - $transportadora     CTE: $cte"
    val linha4 = "Motivo Devolução: $tipoDevolucao"
    val observacao = nota.obsDevolucao ?: ""
    val observacaoNota = if (observacao.isEmpty()) "" else "|Observação: $observacao"

    form = SubWindowForm(
      title = "$linha1|$linha2|$linha3|$linha4$observacaoNota",
      toolBar = {
        integerField("Volume") {
          this.value = nota.volumeDevolucao ?: 0
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.width = "6rem"
          this.isAutoselect = true
          this.isReadOnly = true
        }
        bigDecimalField("Peso") {
          this.value = nota.pesoDevolucao?.toBigDecimal() ?: 0.toBigDecimal()
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.width = "6rem"
          this.isAutoselect = true
          this.isReadOnly = true
        }
        integerField("Cod") {
          this.value = nota.transpDevolucao ?: 0
          this.width = "60px"
          this.isAutoselect = true
          this.isReadOnly = true
        }
        textField("Transportadora Redespacho") {
          this.isReadOnly = true
          this.width = "320px"
          this.value = viewModel.findTransportadora(nota.transpDevolucao)?.nome ?: ""
        }
        textField("CTE") {
          this.width = "120px"
          this.value = nota.cteDevolucao ?: ""
          if (this.value.isNullOrBlank()) {
            this.value = "CTE "
          }
          this.isReadOnly = true
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