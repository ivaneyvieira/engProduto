package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.list
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevDadosViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosDadosDev(val viewModel: TabDevDadosViewModel, val nota: DadosDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(DadosDevProduto::class.java, false)

  fun showDialog(onClose: () -> Unit) {

    form = SubWindowForm(
      title = "NI ${nota.ni}",
      toolBar = {

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
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.NONE

      columnGrid(DadosDevProduto::codigo, header = "Código")
      columnGrid(DadosDevProduto::descricao, header = "Descricao")
      columnGrid(DadosDevProduto::grade, header = "Grade")
      columnGrid(DadosDevProduto::unidade, header = "UN")
      columnGrid(DadosDevProduto::quantidadeDev, header = "Quant")
      columnGrid(DadosDevProduto::valorUnitario, header = "Valor")
      columnGrid(DadosDevProduto::valorTotal, header = "Total")
    }
    this.addAndExpand(gridDetail)

    update()
  }

  fun update() {
    gridDetail.setItems(nota.produtos)
  }

  fun produtos(): List<DadosDevProduto> {
    return gridDetail.list()
  }

  fun fecha() {
    form?.close()
  }
}