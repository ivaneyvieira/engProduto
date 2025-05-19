package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.EMarcaNota
import br.com.astrosoft.produto.model.beans.NotaSaidaDev
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaNFDAbertaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.h5
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.textArea
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.theme.lumo.LumoUtility

class DlgProdutosNFDAberta(val viewModel: TabNotaNFDAbertaViewModel, val nota: NotaSaidaDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFS::class.java, false)
  val lblCancel = if (nota.cancelada == "S") " (Cancelada)" else ""
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm(
      title = "Produtos da expedicao ${nota.nota} loja: ${nota.loja}${lblCancel}",
      header = {
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
              textField("Nota") {
                this.value = nota.nota
                this.isReadOnly = true
                this.width = "120px"
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
                this.value = nota.volume?.format()
                this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
                this.isReadOnly = true
                this.width = "120px"
              }
              textField("Peso") {
                this.value = nota.peso.format()
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
              this.value = ""
              this.isReadOnly = true
              this.isExpand = true
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
      }, onClose = {
        onClose()
      }) {
      VerticalLayout().apply {
        val grid = HorizontalLayout().apply {
          setSizeFull()
          createGridProdutos()
        }
        val obs = HorizontalLayout().apply {
          this.setWidthFull()
          this.addClassNames(LumoUtility.BorderRadius.MEDIUM, LumoUtility.Border.ALL)
          this.isMargin = false
          this.isPadding = true

          h5(nota.observacaoPrint ?: "") {
            this.setSizeFull()
          }
        }
        addAndExpand(grid)
        add(obs)
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

      addItemDoubleClickListener { e ->
        editor.editItem(e.item)
        val editorComponent: Component = e.column.editorComponent
        if (editorComponent is Focusable<*>) {
          (editorComponent as Focusable<*>).focus()
        }
      }

      columnGrid(ProdutoNFS::codigo) {
        this.setHeader("Código")
      }
      columnGrid(ProdutoNFS::barcodeStrList) {
        this.setHeader("Código de Barras")
        this.right()
      }
      columnGrid(ProdutoNFS::descricao) {
        this.setHeader("Descrição")
      }
      columnGrid(ProdutoNFS::grade) {
        this.setHeader("Grade")
      }
      columnGrid(ProdutoNFS::quantidade) {
        this.setHeader("Quant")
      }

      columnGrid(ProdutoNFS::preco) {
        this.setHeader("Preço")
      }
      columnGrid(ProdutoNFS::total) {
        this.setHeader("Total")
      }

      this.setPartNameGenerator {
        val marca = it.marca
        val marcaImpressao = it.marcaImpressao ?: 0
        when {
          marcaImpressao > 0 -> "azul"
          marca == EMarcaNota.CD.num -> "amarelo"
          else -> null
        }
      }
    }
    this.addAndExpand(gridDetail)

    update()
  }

  fun itensSelecionados(): List<ProdutoNFS> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos(todosLocais = true)
    gridDetail.setItems(listProdutos)
  }
}