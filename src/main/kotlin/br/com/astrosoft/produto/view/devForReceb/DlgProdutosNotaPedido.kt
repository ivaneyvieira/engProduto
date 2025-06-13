package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaPedidoViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosNotaPedido(val viewModel: TabNotaPedidoViewModel, var nota: NotaRecebimentoDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaRecebimentoProdutoDev::class.java, false)
  private var edtTransportadora: TextField? = null

  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm(
      header = {
        this.formHeader(nota) { notaModificada: NotaRecebimentoDev ->
          viewModel.saveNota(notaModificada)
        }
      },
      toolBar = {
        integerField("Volume") {
          this.value = nota.volumeDevolucao ?: 0
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.width = "6rem"
          this.isAutoselect = true
          this.valueChangeMode = ValueChangeMode.LAZY

          addValueChangeListener {
            nota.volumeDevolucao = this.value ?: 0
            viewModel.saveNota(nota)
          }
        }
        bigDecimalField("Peso") {
          this.value = nota.pesoDevolucao?.toBigDecimal() ?: 0.toBigDecimal()
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.width = "6rem"
          this.isAutoselect = true
          this.valueChangeMode = ValueChangeMode.LAZY

          addValueChangeListener {
            nota.pesoDevolucao = this.value?.toDouble() ?: 0.0
            viewModel.saveNota(nota)
          }
        }
        integerField("Cod") {
          this.value = nota.transpDevolucao ?: 0
          this.width = "60px"
          this.isAutoselect = true
          this.valueChangeMode = ValueChangeMode.LAZY

          addValueChangeListener {
            nota.transpDevolucao = this.value ?: 0
            viewModel.saveNota(nota)
            edtTransportadora?.value = viewModel.findTransportadora(this.value)?.nome ?: ""
          }
        }
        edtTransportadora = textField("Transportadora Redespacho") {
          this.isReadOnly = true
          this.width = "320px"
          this.value = viewModel.findTransportadora(nota.transpDevolucao)?.nome ?: ""
        }
        datePicker("Data") {
          this.localePtBr()
          this.value = nota.dataDevolucao
          this.width = "120px"

          addValueChangeListener {
            nota.dataDevolucao = this.value
            viewModel.saveNota(nota)
          }
        }
        this.button("Adiciona") {
          this.icon = VaadinIcon.PLUS.create()
          this.addClickListener {
            val dlg = DlgAdicionaProdutoNota(viewModel, nota) {
              gridDetail.dataProvider.refreshAll()
            }
            dlg.open()
          }
        }

        this.button("Remover") {
          this.icon = VaadinIcon.TRASH.create()
          this.addClickListener {
            viewModel.removeProduto()
          }
        }

        this.button("Esp Nota") {
          this.icon = VaadinIcon.FILE_TEXT.create()
          this.addClickListener {
            viewModel.imprimirEspelhoNota(nota)
          }
        }

        this.button("Imp Comp") {
          this.icon = VaadinIcon.FILE_TEXT.create()
          this.addClickListener {
            viewModel.imprimirRelatorioCompleto(nota)
          }
        }

        this.button("Imp Red") {
          this.icon = VaadinIcon.FILE_TEXT.create()
          this.addClickListener {
            viewModel.imprimirRelatorioReduzido(nota)
          }
        }

        this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "planilhaDev") {
          val produtos = gridDetail.dataProvider.fetchAll()
          if (produtos.isEmpty()) {
            ByteArray(0)
          } else {
            viewModel.geraPlanilha(produtos)
          }
        }
      },
      headerGrid = {
        this.isMargin = false
        this.isPadding = false
        horizontalBlock {
          this.setWidthFull()
          this.isSpacing = true
          textField("Produtos") {
            this.value = nota.valorTotalProduto.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("Frete") {
            this.value = nota.valorFrete.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("Desconto") {
            this.value = nota.valorDesconto.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("Despesas") {
            this.value = nota.outrasDespesas.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("Base ICMS") {
            this.value = nota.baseIcmsProdutos.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("ICMS") {
            this.value = nota.valorIcmsProdutos.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("Base ST") {
            this.value = nota.baseIcmsSubstProduto.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("ST") {
            this.value = nota.icmsSubstProduto.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("IPI") {
            this.value = nota.valorIpiProdutos.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("Total Nota") {
            this.value = nota.valorTotalNota.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
        }
      },
      onClose = {
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

      columnGrid(NotaRecebimentoProdutoDev::seq, "Item").right()
      columnGrid(NotaRecebimentoProdutoDev::loja, "Lj").right()
      addColumnButton(VaadinIcon.DATE_INPUT, "Conferência", "Conf") { produto ->
        val dlgConferencia = DlgConferenciaProduto(viewModel, produto) {
          update()
        }
        dlgConferencia.open()
      }
      columnGrid(NotaRecebimentoProdutoDev::nfEntrada, "NFO").right()
      //columnGrid(NotaRecebimentoProdutoDev::vendno, "For").right()
      columnGrid(NotaRecebimentoProdutoDev::ni, "NI").right()
      columnGrid(NotaRecebimentoProdutoDev::codigo, "Código").right()
      columnGrid(NotaRecebimentoProdutoDev::grade, "Grade", width = "80px")
      columnGrid(NotaRecebimentoProdutoDev::quantDevolucao, "Quant")
      columnGrid(NotaRecebimentoProdutoDev::valorUnit, "Valor Unit", pattern = "#,##0.0000", width = "90px")
      columnGrid(NotaRecebimentoProdutoDev::valorTotalDevolucao, "Valor Total", width = "90px")
      columnGrid(NotaRecebimentoProdutoDev::descricao, "Descrição")
      columnGrid(NotaRecebimentoProdutoDev::cfop, "CFOP")
      columnGrid(NotaRecebimentoProdutoDev::cst, "CST")
      columnGrid(NotaRecebimentoProdutoDev::un, "UN")
      columnGrid(NotaRecebimentoProdutoDev::valorDescontoDevolucao, "Desc", width = "60px")
      columnGrid(NotaRecebimentoProdutoDev::freteDevolucao, "Frete", width = "60px")
      columnGrid(NotaRecebimentoProdutoDev::outDespDevolucao, "Desp", width = "60px")
      columnGrid(NotaRecebimentoProdutoDev::baseIcmsDevolucao, "Base ICMS", width = "90px")
      columnGrid(NotaRecebimentoProdutoDev::valorMVA, "MVA", width = "60px")
      columnGrid(NotaRecebimentoProdutoDev::baseIcmsSubst, "Base ST", width = "90px")
      columnGrid(NotaRecebimentoProdutoDev::icmsSubstDevolucao, "Valor ST", width = "90px")
      columnGrid(NotaRecebimentoProdutoDev::valIcmsDevolucao, "V. ICMS", width = "70px")
      columnGrid(NotaRecebimentoProdutoDev::valIPIDevolucao, "V. IPI", width = "60px")
      columnGrid(NotaRecebimentoProdutoDev::icms, "ICMS", width = "60px")
      columnGrid(NotaRecebimentoProdutoDev::ipi, "IPI", width = "50px")
      columnGrid(NotaRecebimentoProdutoDev::totalGeralDevolucao, "Total", width = "90px")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<NotaRecebimentoProdutoDev> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos.sortedBy { it.seq ?: 0 }
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): NotaRecebimentoProdutoDev? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { prd ->
      prd.containBarcode(codigoBarra)
    }
  }

  fun updateProduto(): NotaRecebimentoDev? {
    nota = nota.refreshProdutosDev() ?: return null
    update()
    return nota
  }
}