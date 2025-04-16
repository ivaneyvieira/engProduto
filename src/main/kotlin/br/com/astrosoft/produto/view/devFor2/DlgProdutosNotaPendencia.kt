package br.com.astrosoft.produto.view.devFor2

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev
import br.com.astrosoft.produto.viewmodel.devFor2.TabNotaPendenciaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosNotaPendencia(val viewModel: TabNotaPendenciaViewModel, val nota: NotaRecebimentoDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaRecebimentoProdutoDev::class.java, false)
  private var edtTransportadora: TextField? = null

  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm(
      header = {
        this.setWidthFull()
        horizontalBlock {
          this.setWidthFull()
          this.isSpacing = true
          verticalBlock {
            this.isPadding = false
            this.isMargin = false
            this.isSpacing = false

            horizontalBlock {
              this.isSpacing = true

              integerField("NI") {
                this.isReadOnly = true
                this.width = "6rem"
                this.value = nota.ni ?: 0
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              }
              textField("NFO") {
                this.isReadOnly = true
                this.width = "6rem"
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
                this.value = nota.nfEntrada ?: ""
              }
              textField("Emissão") {
                this.isReadOnly = true
                this.width = "7rem"
                this.value = nota.emissao.format()
              }
              textField("Entrada") {
                this.isReadOnly = true
                this.width = "7rem"
                this.value = nota.data.format()
              }
              integerField("Cod") {
                this.isReadOnly = true
                this.width = "3.5rem"
                this.value = nota.vendno
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              }
              textField("Fornecedor") {
                this.isReadOnly = true
                this.width = "20rem"
                this.value = nota.fornecedor
              }
            }

            horizontalBlock {
              this.isSpacing = true

              textField("Cod") {
                this.isReadOnly = true
                this.width = "3.5rem"
                this.value = nota.transp?.toString()
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              }
              textField("Transportadora") {
                this.isReadOnly = true
                this.width = "20rem"
                this.value = nota.transportadora
              }
              textField("CTE") {
                this.isReadOnly = true
                this.width = "7rem"
                this.value = nota.cte?.toString()
              }
              textField("Ped Compra") {
                this.isReadOnly = true
                this.width = "7rem"
                this.value = nota.pedComp?.toString()
              }
            }

            horizontalBlock {
              this.isSpacing = true

              textField("NFD") {
                this.isReadOnly = true
                this.width = "6rem"
                this.value = nota.notaDevolucao ?: ""
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
              }
              textField("Emissão") {
                this.isReadOnly = true
                this.width = "7rem"
                this.value = nota.emissaoDevolucao.format()
              }
              datePicker("Coleta") {
                this.localePtBr()
                this.width = "10rem"
                this.value = nota.dataColeta

                addValueChangeListener {
                  nota.dataColeta = this.value
                  viewModel.saveNota(nota)
                }
              }
              textField("Motivo Devolução") {
                this.isReadOnly = true
                this.width = "15rem"
                this.value = nota.tipoDevolucaoName ?: ""
              }
            }
          }
          textArea("Dados Adicionais") {
            this.isExpand = true
            this.isReadOnly = true
            this.setSizeFull()
            this.value = nota.obsDevolucao ?: ""
          }
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
        textField("CTE") {
          this.width = "120px"
          this.value = nota.cteDevolucao ?: ""
          if (this.value.isNullOrBlank()) {
            this.value = "CTE "
          }
          this.valueChangeMode = ValueChangeMode.LAZY

          addValueChangeListener {
            nota.cteDevolucao = this.value ?: ""
            viewModel.saveNota(nota)
          }
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

      columnGrid(NotaRecebimentoProdutoDev::ni, "NI").right()
      columnGrid(NotaRecebimentoProdutoDev::codigo, "Código").right()
      columnGrid(NotaRecebimentoProdutoDev::descricao, "Descrição")
      columnGrid(NotaRecebimentoProdutoDev::grade, "Grade", width = "80px")
      columnGrid(NotaRecebimentoProdutoDev::cfop, "CFOP")
      columnGrid(NotaRecebimentoProdutoDev::cst, "CST")
      columnGrid(NotaRecebimentoProdutoDev::un, "UN")
      columnGrid(NotaRecebimentoProdutoDev::quantDevolucao, "Quant")
      columnGrid(NotaRecebimentoProdutoDev::valorUnit, "Valor Unit", pattern = "#,##0.0000", width = "90px")
      columnGrid(NotaRecebimentoProdutoDev::valorTotalDevolucao, "Valor Total", width = "90px")
      columnGrid(NotaRecebimentoProdutoDev::valorDescontoDevolucao, "Desc", width = "60px")
      columnGrid(NotaRecebimentoProdutoDev::freteDevolucao, "Frete", width = "60px")
      columnGrid(NotaRecebimentoProdutoDev::outDespDevolucao, "Desp", width = "60px")
      columnGrid(NotaRecebimentoProdutoDev::baseIcmsDevolucao, "Base ICMS", width = "90px")
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
    val listProdutos = nota.produtos
    gridDetail.setItems(listProdutos)
    gridDetail.getColumnBy(NotaRecebimentoProdutoDev::valorTotalDevolucao).setFooter(
      listProdutos.sumOf { it.valorTotalDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProdutoDev::valorDescontoDevolucao).setFooter(
      listProdutos.sumOf { it.valorDescontoDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProdutoDev::freteDevolucao).setFooter(
      listProdutos.sumOf { it.freteDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProdutoDev::outDespDevolucao).setFooter(
      listProdutos.sumOf { it.outDespDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProdutoDev::baseIcmsDevolucao).setFooter(
      listProdutos.sumOf { it.baseIcmsDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProdutoDev::icmsSubstDevolucao).setFooter(
      listProdutos.sumOf { it.icmsSubstDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProdutoDev::valIcmsDevolucao).setFooter(
      listProdutos.sumOf { it.valIcmsDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProdutoDev::valIPIDevolucao).setFooter(
      listProdutos.sumOf { it.valIPIDevolucao ?: 0.0 }.format("#,##0.00")
    )
    gridDetail.getColumnBy(NotaRecebimentoProdutoDev::totalGeralDevolucao).setFooter(
      listProdutos.sumOf { it.totalGeralDevolucao ?: 0.0 }.format("#,##0.00")
    )
  }

  fun produtosCodigoBarras(codigoBarra: String): NotaRecebimentoProdutoDev? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { prd ->
      prd.containBarcode(codigoBarra)
    }
  }

  fun updateProduto(): NotaRecebimentoDev? {
    val nota = nota.refreshProdutosDev()
    update()
    return nota
  }
}