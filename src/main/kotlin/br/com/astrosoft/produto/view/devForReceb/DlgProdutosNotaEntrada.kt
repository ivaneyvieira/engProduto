package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.framework.view.vaadin.helper.superDoubleField
import br.com.astrosoft.produto.model.beans.EMotivoDevolucao
import br.com.astrosoft.produto.model.beans.NotaRecebimento
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaEntradaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosNotaEntrada(val viewModel: TabNotaEntradaViewModel, var nota: NotaRecebimento) {
  private var edtPesquisa: TextField? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaRecebimentoProduto::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val numeroNota = nota.nfEntrada ?: ""
    val emissao = nota.emissao.format()
    val numeroInterno = nota.ni

    form = SubWindowForm(
      header = {
        this.isPadding = false
        this.isMargin = false
        this.isSpacing = false

        horizontalBlock {
          this.isSpacing = true

          integerField("NI") {
            this.isReadOnly = true
            this.width = "6rem"
            this.value = numeroInterno
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          }
          textField("NFO") {
            this.isReadOnly = true
            this.width = "6rem"
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.value = numeroNota
          }
          textField("Emissão") {
            this.isReadOnly = true
            this.width = "7rem"
            this.value = emissao
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
          textField("Vol Inf") {
            this.isReadOnly = true
            this.width = "7rem"
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.value = nota.volume.format()
          }
          integerField("Vol Receb") {
            this.width = "7rem"
            if ((nota.volumeDevolucao ?: 0) == 0) {
              this.value = nota.volume ?: 0
            } else {
              this.value = nota.volumeDevolucao ?: 0
            }
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.valueChangeMode = ValueChangeMode.LAZY
            this.isAutoselect = true
            this.isClearButtonVisible = true
            addValueChangeListener {
              if (it.isFromClient) {
                if( (it.value ?: 0) == 0) {
                  nota.volumeDevolucao = nota.volume ?: 0
                } else{
                  nota.volumeDevolucao = it.value ?: 0
                }
                viewModel.saveNota(nota)
                this.value = nota.volumeDevolucao ?: 0
              }
            }
          }
          superDoubleField("Peso") {
            this.width = "7rem"
            if ((nota.pesoDevolucao ?: 0.00) == 0.00) {
              this.value = nota.peso ?: 0.00
            } else {
              this.value = nota.pesoDevolucao ?: 0.00
            }
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.valueChangeMode = ValueChangeMode.LAZY
            this.isAutoselect = true
            this.isClearButtonVisible = true
            addValueChangeListener {
              if (it.isFromClient) {
                if ((it.value ?: 0.00) == 0.00) {
                  nota.pesoDevolucao = nota.peso ?: 0.00
                } else {
                  nota.pesoDevolucao = it.value ?: 0.00
                }
                viewModel.saveNota(nota)
                this.value = nota.pesoDevolucao ?: 0.00
              }
            }
          }
          textField("Ped Compra") {
            this.isReadOnly = true
            this.width = "7rem"
            this.value = nota.pedComp?.toString()
          }
        }
      },
      toolBar = {
        edtPesquisa = textField("Pesquisa") {
          this.valueChangeMode = ValueChangeMode.LAZY
          this.addValueChangeListener {
            update()
          }
        }
        select("Motivo Devolução") {
          this.setItems(EMotivoDevolucao.entries)

          this.addValueChangeListener {
            val produtos = gridDetail.selectedItems.toList()
            viewModel.devolucaoProduto(produtos, it.value)
          }
        }
        button("Desfazer") {
          this.icon = VaadinIcon.ARROW_BACKWARD.create()
          this.addClickListener {
            val produtos = gridDetail.selectedItems.toList()
            viewModel.desfazerDevolucao(produtos)
          }
        }
        this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "planilhaEntrada") {
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
            this.value = nota.valorTotal.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("Frete") {
            this.value = nota.frete.format()
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
            this.value = nota.outDesp.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("Base ICMS") {
            this.value = nota.baseIcms.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("ICMS") {
            this.value = nota.valIcms.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("Base ST") {
            this.value = nota.baseSubst.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("ST") {
            this.value = nota.icmsSubst.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("IPI") {
            this.value = nota.valIPI.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.formatFont()
          }
          textField("Total Nota") {
            this.value = nota.totalGeral.format()
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

      columnGrid(NotaRecebimentoProduto::codigo, "Código").right()
      columnGrid(NotaRecebimentoProduto::barcodeStrListEntrada, "Código de Barras").right()
      columnGrid(NotaRecebimentoProduto::refFabrica, "Ref Fabrica").right()
      columnGrid(NotaRecebimentoProduto::descricao, "Descrição")
      columnGrid(NotaRecebimentoProduto::grade, "Grade", width = "80px")
      columnGrid(NotaRecebimentoProduto::cfop, "CFOP")
      columnGrid(NotaRecebimentoProduto::cst, "CST")
      columnGrid(NotaRecebimentoProduto::un, "UN")
      columnGrid(NotaRecebimentoProduto::quant, "Quant")
      columnGrid(NotaRecebimentoProduto::valorUnit, "Valor Unit", pattern = "#,##0.0000", width = "90px")
      columnGrid(NotaRecebimentoProduto::valorTotal, "Valor Total", width = "90px")
      columnGrid(NotaRecebimentoProduto::valorDesconto, "Desc", width = "60px")
      columnGrid(NotaRecebimentoProduto::frete, "Frete", width = "60px")
      columnGrid(NotaRecebimentoProduto::outDesp, "Desp", width = "60px")
      columnGrid(NotaRecebimentoProduto::baseIcms, "Base ICMS", width = "90px")
      columnGrid(NotaRecebimentoProduto::baseSubst, "Base ST", width = "90px")
      columnGrid(NotaRecebimentoProduto::icmsSubst, "Valor ST", width = "90px")
      columnGrid(NotaRecebimentoProduto::valIcms, "V. ICMS", width = "70px")
      columnGrid(NotaRecebimentoProduto::valIPI, "V. IPI", width = "60px")
      columnGrid(NotaRecebimentoProduto::icms, "ICMS", width = "60px")
      columnGrid(NotaRecebimentoProduto::ipi, "IPI", width = "50px")
      columnGrid(NotaRecebimentoProduto::totalGeral, "Total", width = "90px")

      this.setPartNameGenerator {
        if (it.devolucoes().isNotEmpty()) {
          "amarelo"
        } else null
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<NotaRecebimentoProduto> {
    return gridDetail.selectedItems.toList()
  }

  private fun NotaRecebimentoProduto.textoPesquisa(): String {
    return "$codigo|$barcodeStrListEntrada|$refFabrica|$descricao|$grade|$cfop|$cst|$un"
  }

  fun update() {
    val pesquisa = edtPesquisa?.value?.trim() ?: ""
    val listProdutos = nota.produtos.filter {
      pesquisa == "" || it.textoPesquisa().contains(pesquisa, true)
    }
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): NotaRecebimentoProduto? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { prd ->
      prd.containBarcode(codigoBarra)
    }
  }

  fun updateProduto(): NotaRecebimento? {
    nota = nota.refreshProdutos() ?: return null
    update()
    return nota
  }
}