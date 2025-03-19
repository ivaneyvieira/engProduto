package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.FiltroAcerto
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgEstoqueAcerto(val viewModel: TabEstoqueAcertoViewModel, val acerto: ProdutoEstoqueAcerto) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoEstoqueAcerto::class.java, false)

  fun showDialog(onClose: () -> Unit = {}) {
    this.onClose = onClose
    val numero = acerto.numero
    val loja = acerto.estoqueLoja

    form = SubWindowForm(
      "Produtos do Acerto $numero - Loja $loja",
      toolBar = {
        button("Imprimir"){
          this.icon = VaadinIcon.PRINT.create()
          this.addClickListener {
            viewModel.imprimir(acerto)
          }
        }
        button ("Relatório"){
          this.icon = VaadinIcon.FILE_TEXT.create()
          this.addClickListener {
            viewModel.imprimirRelatorio(acerto)
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
      isMultiSort = false

      columnGrid(ProdutoEstoqueAcerto::codigo, "Código")
      columnGrid(ProdutoEstoqueAcerto::descricao, "Descrição")
      columnGrid(ProdutoEstoqueAcerto::grade, "Grade")
      columnGrid(ProdutoEstoqueAcerto::estoqueSis, "Est Sist")
      columnGrid(ProdutoEstoqueAcerto::estoqueCD, "Est CD")
      columnGrid(ProdutoEstoqueAcerto::estoqueLoja, "Est Loja")
      columnGrid(ProdutoEstoqueAcerto::diferenca, "Diferença")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoEstoqueAcerto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val filtro = FiltroAcerto(
      numLoja = acerto.numloja ?: 0,
      numero = acerto.numero ?: 0
    )
    val produtos = ProdutoEstoqueAcerto.findAll(filtro)
    gridDetail.setItems(produtos)
  }

  private fun closeForm() {
    onClose?.invoke()
    form?.close()
  }
}