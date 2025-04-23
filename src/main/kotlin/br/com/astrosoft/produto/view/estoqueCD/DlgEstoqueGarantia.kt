package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.EstoqueGarantia
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueGarantia
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueGarantiaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgEstoqueGarantia(val viewModel: TabEstoqueGarantiaViewModel, val garantia: EstoqueGarantia) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoEstoqueGarantia::class.java, false)

  fun showDialog(onClose: () -> Unit = {}) {
    this.onClose = onClose
    val numero = garantia.numero
    val loja = garantia.lojaSigla

    form = SubWindowForm(
      "Produtos do Garantia $numero - Loja $loja",
      toolBar = {
        button("Pedido") {
          this.icon = VaadinIcon.PRINT.create()
          this.addClickListener {
            viewModel.imprimirPedido(garantia)
          }
        }

        this.button("Adiciona") {
          this.icon = VaadinIcon.PLUS.create()
          this.addClickListener {
            val dlg = DlgAdicionaGarantia(viewModel, garantia) {
              gridDetail.dataProvider.refreshAll()
            }
            dlg.open()
          }
        }

        this.button("Remove") {
          this.icon = VaadinIcon.TRASH.create()
          this.addClickListener {
            viewModel.removeGarantia()
          }
        }

        button("Relatório") {
          this.icon = VaadinIcon.FILE_TEXT.create()
          this.addClickListener {
            viewModel.imprimirRelatorio(garantia)
          }
        }

        this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "acertoEstoque") {
          val produtos = estoqueGarantias()
          viewModel.geraPlanilha(produtos)
        }

        this.button("Copia Est") {
          this.icon = VaadinIcon.COPY.create()
          this.addClickListener {
            viewModel.copiaEstoque()
          }
        }
      },
      onClose = {
        closeForm()
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
      this.setSelectionMode(Grid.SelectionMode.MULTI)
      isMultiSort = false

      columnGrid(ProdutoEstoqueGarantia::lojaReceb, "Loja")
      columnGrid(ProdutoEstoqueGarantia::niReceb, "NI")
      columnGrid(ProdutoEstoqueGarantia::nfoReceb, "NFO").right()
      columnGrid(ProdutoEstoqueGarantia::entradaReceb, "Entrada", width = null)
      columnGrid(ProdutoEstoqueGarantia::cfopReceb, "CFOP").right()
      columnGrid(ProdutoEstoqueGarantia::forReceb, "For NFO")

      columnGrid(ProdutoEstoqueGarantia::ref, "Ref Fab").right()
      columnGrid(ProdutoEstoqueGarantia::codigo, "Código").right()
      columnGrid(ProdutoEstoqueGarantia::descricao, "Descrição")
      columnGrid(ProdutoEstoqueGarantia::grade, "Grade")
      addColumnButton(VaadinIcon.DATE_INPUT, "Conferência", "Conf") { produto ->
        val dlgConferencia = DlgConferenciaGarantia(viewModel, produto) {
          gridDetail.dataProvider.refreshAll()
        }
        dlgConferencia.open()
      }
      columnGrid(ProdutoEstoqueGarantia::estoqueLoja, "Est Loja")
      columnGrid(ProdutoEstoqueGarantia::estoqueLojas, "Est Lojas")
      columnGrid(ProdutoEstoqueGarantia::estoqueDev, "Est Dev")
      columnGrid(ProdutoEstoqueGarantia::valorUnitario, "V. Unit")
      columnGrid(ProdutoEstoqueGarantia::valorTotal, "V. Total")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoEstoqueGarantia> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val produtos = estoqueGarantias()
    gridDetail.setItems(produtos)
  }

  private fun estoqueGarantias(): List<ProdutoEstoqueGarantia> {
    return garantia.findProdutos()
  }

  private fun closeForm() {
    onClose?.invoke()
    form?.close()
  }

  fun produtosSelecionado(): List<ProdutoEstoqueGarantia> {
    return gridDetail.selectedItemsSort()
  }

  fun updateGarantia(acertos: List<EstoqueGarantia>) {
    val garantia = acertos.firstOrNull {
      it.numloja == this.garantia.numloja && it.numero == this.garantia.numero
    }
    gridDetail.setItems(garantia?.findProdutos().orEmpty())
  }
}