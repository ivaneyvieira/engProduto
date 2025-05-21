package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.NotaSaidaDev
import br.com.astrosoft.produto.model.beans.NotaSaidaDevProduto
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaNFDAbertaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosNFDAberta(val viewModel: TabNotaNFDAbertaViewModel, val nota: NotaSaidaDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaSaidaDevProduto::class.java, false)

  init {
    nota.updateProdutos()
  }

  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm(
      header = {
        this.isMargin = false
        this.isPadding = false
        horizontalBlock {
          this.setWidthFull()
          this.isSpacing = true
          verticalBlock {
            //Campos
            this.width = "50%"
            this.isSpacing = true
            horizontalBlock {
              //Linha01
              this.setWidthFull()
              this.isSpacing = true
              textField("Loja") {
                this.value = nota.loja.toString()
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
                this.isReadOnly = true
                this.width = "60px"
              }
              textField("Nota") {
                this.value = nota.nota
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
                this.isReadOnly = true
                this.width = "100px"
              }
              textField("Emissão") {
                this.value = nota.dataEmissao.format()
                this.isReadOnly = true
                this.width = "120px"
              }
              textField("Cod") {
                this.value = nota.cliente?.toString() ?: ""
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
                this.isReadOnly = true
                this.width = "100px"
              }
              textField("Cliente") {
                this.value = nota.nomeCliente ?: ""
                this.isReadOnly = true
                this.isExpand = true
              }
            }
            horizontalBlock {
              //Linha02
              this.setWidthFull()
              this.isSpacing = true
              textField("Cod") {
                this.value = nota.codTransportadora?.toString() ?: ""
                this.isReadOnly = true
                this.width = "100px"
              }
              textField("Transportadora") {
                this.value = nota.nomeTransportadora ?: ""
                this.isReadOnly = true
                this.isExpand = true
              }
              textField("Volume") {
                this.value = nota.volume?.format("0")
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
                this.isReadOnly = true
                this.width = "120px"
              }
              textField("Peso") {
                this.value = nota.peso.format("0.0000")
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
                this.isReadOnly = true
                this.width = "120px"
              }
            }
          }
          verticalBlock {
            //Observação01
            this.width = "25%"
            this.setHeightFull()
            textArea("Dados Adicionais") {
              this.setSizeFull()
              this.value = nota.observacaoPrint ?: ""
              this.isReadOnly = true
              this.isExpand = true
            }
          }
          verticalBlock {
            //Observação02
            this.width = "25%"
            this.setHeightFull()
            textArea("Observação") {
              this.setSizeFull()
              this.value = nota.observacaoAdd ?: ""
              this.isReadOnly = false
              this.isExpand = true
              this.valueChangeMode = ValueChangeMode.LAZY
              addValueChangeListener {
                nota.observacaoAdd = it.value ?: ""
                viewModel.saveObs(nota)
              }
            }
          }
        }
      },
      toolBar = {
        button("Imprimir") {
          this.icon = VaadinIcon.PRINT.create()
          this.isVisible = nota.cancelada == "N"
          onClick {
            val itensSelecionados = gridDetail.selectedItems.toList()
            viewModel.imprimeProdutosNota(nota, itensSelecionados)
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
            this.value = nota.total.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.width = "100px"
            this.formatFont()
          }
          textField("Frete") {
            this.value = nota.frete.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.width = "100px"
            this.formatFont()
          }
          textField("Desconto") {
            this.value = nota.desconto.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.width = "100px"
            this.formatFont()
          }
          textField("Despesas") {
            this.value = nota.despesas.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.width = "100px"
            this.formatFont()
          }
          textField("Base ICMS") {
            this.value = nota.baseIcms.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.width = "100px"
            this.formatFont()
          }
          textField("ICMS") {
            this.value = nota.valorIcms.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.width = "100px"
            this.formatFont()
          }
          textField("Base ST") {
            this.value = nota.baseSubst.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.width = "100px"
            this.formatFont()
          }
          textField("ST") {
            this.value = nota.valorSubst.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.width = "100px"
            this.formatFont()
          }
          textField("IPI") {
            this.value = nota.valorIpi.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.width = "100px"
            this.formatFont()
          }
          textField("Total Nota") {
            this.value = nota.totalGeral.format()
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isReadOnly = true
            this.width = "100px"
            this.formatFont()
          }
        }
      },
      onClose = {
        onClose()
      }) {
      VerticalLayout().apply {
        this.isMargin = false
        this.isPadding = false

        val grid = HorizontalLayout().apply {
          setSizeFull()
          createGridProdutos()
        }
        addAndExpand(grid)
      }
    }
    form?.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI

      columnGrid(NotaSaidaDevProduto::codigo) {
        this.setHeader("Código")
      }
      columnGrid(NotaSaidaDevProduto::barcodeStrList) {
        this.setHeader("Código de barras")
      }
      columnGrid(NotaSaidaDevProduto::descricao) {
        this.setHeader("Descrição")
      }
      columnGrid(NotaSaidaDevProduto::grade) {
        this.setHeader("Grade")
      }
      columnGrid(NotaSaidaDevProduto::cfop) {
        this.setHeader("CFOP")
      }
      columnGrid(NotaSaidaDevProduto::cst) {
        this.setHeader("CST")
      }
      columnGrid(NotaSaidaDevProduto::un) {
        this.setHeader("UN")
      }
      columnGrid(NotaSaidaDevProduto::quantidade) {
        this.setHeader("Quant")
      }
      columnGrid(NotaSaidaDevProduto::preco) {
        this.setHeader("Valor Unit")
      }
      columnGrid(NotaSaidaDevProduto::total) {
        this.setHeader("Valor Total")
      }
      columnGrid(NotaSaidaDevProduto::desconto) {
        this.setHeader("Desc")
      }
      columnGrid(NotaSaidaDevProduto::frete) {
        this.setHeader("Frete")
      }
      columnGrid(NotaSaidaDevProduto::despesas) {
        this.setHeader("Desp")
      }
      columnGrid(NotaSaidaDevProduto::baseIcms) {
        this.setHeader("Base ICMS")
      }
      columnGrid(NotaSaidaDevProduto::valorSubst) {
        this.setHeader("Valor ST")
      }
      columnGrid(NotaSaidaDevProduto::valorIcms) {
        this.setHeader("V.ICMS")
      }
      columnGrid(NotaSaidaDevProduto::valorIpi) {
        this.setHeader("V.IPI")
      }
      columnGrid(NotaSaidaDevProduto::aliquotaIcms) {
        this.setHeader("ICMS")
      }
      columnGrid(NotaSaidaDevProduto::aliquotaIpi) {
        this.setHeader("IPI")
      }
      columnGrid(NotaSaidaDevProduto::totalGeral) {
        this.setHeader("Total")
      }
    }
    this.addAndExpand(gridDetail)

    update()
  }

  fun itensSelecionados(): List<NotaSaidaDevProduto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    nota.updateProdutos()
    val listProdutos = nota.obetemProdutos()
    gridDetail.setItems(listProdutos)
  }
}

fun @VaadinDsl TextField.formatFont() {
  this.addClassName("grande")
  this.width = "120px"
}
