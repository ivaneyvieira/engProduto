package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.list
import br.com.astrosoft.produto.model.beans.DadosProdutosRessuprimento
import br.com.astrosoft.produto.model.beans.DadosRessuprimento
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoBarcode
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoCodigo
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoDescricao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoGrade
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoLocalizacao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoQtRecebido
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoRessupViewModel
import com.github.mvysny.kaributools.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosRessuEdit(val viewModel: TabRessuprimentoRessupViewModel, val ressuprimento: DadosRessuprimento) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(DadosProdutosRessuprimento::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val pedido = "${ressuprimento.pedido}/${ressuprimento.loja}"
    form = SubWindowForm(
      title = "Pedido $pedido",
      toolBar = {
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
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI

      columnGrid(DadosProdutosRessuprimento::codigo, "Código")
      columnGrid(DadosProdutosRessuprimento::descricao, "Descrição", width = "220px")
      columnGrid(DadosProdutosRessuprimento::grade, "Grade")
      columnGrid(DadosProdutosRessuprimento::qttyVendaMes, "Venda no Mes")
      columnGrid(DadosProdutosRessuprimento::qttyVendaMesAnt, "Venda mes Ant")
      columnGrid(DadosProdutosRessuprimento::qttyVendaMedia, "Venda Media")
      columnGrid(DadosProdutosRessuprimento::estoque, "Estoque Atual")
      columnGrid(DadosProdutosRessuprimento::qttySugerida, "Sugestão")
      columnGrid(DadosProdutosRessuprimento::qttyPedida, "Qtde Pedida")
      columnGrid(DadosProdutosRessuprimento::estoqueLJ, "Estoque MF")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<DadosProdutosRessuprimento> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = ressuprimento.produtos
    gridDetail.setItems(listProdutos)
  }

  fun updateProduto(produto: DadosProdutosRessuprimento) {
    gridDetail.dataProvider.refreshItem(produto)
    gridDetail.isMultiSort = true
    update()
    val index = gridDetail.list().indexOf(produto)
    gridDetail.scrollToIndex(index)
    gridDetail.select(produto)
  }

  fun itensSelecionados(): List<DadosProdutosRessuprimento> {
    return gridDetail.selectedItems.toList()
  }
}