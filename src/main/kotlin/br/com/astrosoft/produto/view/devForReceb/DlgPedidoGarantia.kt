package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.PedidoGarantia
import br.com.astrosoft.produto.model.beans.ProdutoPedidoGarantia
import br.com.astrosoft.produto.viewmodel.devFor2.TabPedidoGarantiaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgPedidoGarantia(val viewModel: TabPedidoGarantiaViewModel, val garantia: PedidoGarantia) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoPedidoGarantia::class.java, false)

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

      columnGrid(ProdutoPedidoGarantia::lojaReceb, "Loja")
      columnGrid(ProdutoPedidoGarantia::niReceb, "NI")
      columnGrid(ProdutoPedidoGarantia::nfoReceb, "NFO").right()
      columnGrid(ProdutoPedidoGarantia::entradaReceb, "Entrada", width = null)
      columnGrid(ProdutoPedidoGarantia::cfopReceb, "CFOP").right()
      columnGrid(ProdutoPedidoGarantia::forReceb, "For NFO")

      columnGrid(ProdutoPedidoGarantia::ref, "Ref Fab").right()
      columnGrid(ProdutoPedidoGarantia::codigo, "Código").right()
      columnGrid(ProdutoPedidoGarantia::descricao, "Descrição")
      columnGrid(ProdutoPedidoGarantia::grade, "Grade")
      addColumnButton(VaadinIcon.DATE_INPUT, "Conferência", "Conf") { produto ->
        val dlgConferencia = DlgConferenciaGarantia(viewModel, produto) {
          gridDetail.dataProvider.refreshAll()
          val total = gridDetail.dataProvider.fetchAll().sumOf { it.valorTotal }
          getColumnBy(ProdutoPedidoGarantia::valorTotal).setFooter(total.format())
        }
        dlgConferencia.open()
      }
      columnGrid(ProdutoPedidoGarantia::estoqueLoja, "Est Loja")
      columnGrid(ProdutoPedidoGarantia::estoqueLojas, "Est Lojas")
      columnGrid(ProdutoPedidoGarantia::estoqueDev, "Est Dev")
      columnGrid(ProdutoPedidoGarantia::valorUnitario, "V. Unit")
      columnGrid(ProdutoPedidoGarantia::valorTotal, "V. Total")

      this.dataProvider.addDataProviderListener {
        val total = estoqueGarantias().sumOf { it.valorTotal }
        getColumnBy(ProdutoPedidoGarantia::valorTotal).setFooter(total.format())
      }
    }
    this.addAndExpand(gridDetail)

    update()
  }

  fun produtosSelecionados(): List<ProdutoPedidoGarantia> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val produtos = estoqueGarantias()
    gridDetail.setItems(produtos)
    val total = produtos.sumOf { it.valorTotal }
    gridDetail.getColumnBy(ProdutoPedidoGarantia::valorTotal).setFooter(total.format())
  }

  private fun estoqueGarantias(): List<ProdutoPedidoGarantia> {
    return garantia.findProdutos()
  }

  private fun closeForm() {
    onClose?.invoke()
    form?.close()
  }

  fun produtosSelecionado(): List<ProdutoPedidoGarantia> {
    return gridDetail.selectedItemsSort()
  }

  fun updateGarantia(acertos: List<PedidoGarantia>) {
    val garantia = acertos.firstOrNull {
      it.numloja == this.garantia.numloja && it.numero == this.garantia.numero
    }
    gridDetail.setItems(garantia?.findProdutos().orEmpty())
  }
}