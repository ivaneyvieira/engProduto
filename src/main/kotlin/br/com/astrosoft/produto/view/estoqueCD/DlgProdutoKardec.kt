package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.ViewThread
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.viewmodel.block
import br.com.astrosoft.framework.viewmodel.update
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoKardec
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueSaldoViewModel
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import java.time.LocalDate

class DlgProdutoKardec(val viewModel: TabEstoqueSaldoViewModel, val produto: ProdutoEstoque) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoKardec::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val codigo = produto.codigo ?: 0
    val descricao = produto.descricao ?: ""
    val grade = produto.grade.let { gd ->
      if (gd.isNullOrBlank()) "" else " - $gd"
    }
    val locApp = produto.locApp
    val locSaci = produto.locSaci

    val localizacao = if (locApp.isNullOrBlank()) locSaci ?: "" else locApp
    val dataInicial = produto.dataInicial ?: LocalDate.now().withDayOfMonth(1)

    form = SubWindowForm(
      "$codigo $descricao$grade ($localizacao) Data Inicial: ${dataInicial.format()} Estoque: ${produto.saldo ?: 0}",
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
      this.format()
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)

      columnGrid(ProdutoKardec::loja, "Loja")
      columnGrid(ProdutoKardec::userLogin, "Usuário")
      columnGrid(ProdutoKardec::data, "Data")
      columnGrid(ProdutoKardec::doc, "Doc")
      columnGrid(ProdutoKardec::tipo, "Tipo")
      columnGrid(ProdutoKardec::vencimento, "Venc", pattern = "MM/yyyy", width = null)
      columnGrid(ProdutoKardec::qtde, "Qrd")
      columnGrid(ProdutoKardec::saldo, "Saldo")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoKardec> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    /*
    ViewThread.execAsync(ui = UI.getCurrent()) {
      this.block {
        viewModel.kardec(produto)
      }

      this.update { list: List<ProdutoKardec> ->
        gridDetail.setItems(list)
      }
    }*/
    gridDetail.setItems(viewModel.kardec(produto))
  }

  fun close() {
    onClose?.invoke()
    form?.close()
  }
}