package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueSaldoViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import java.time.LocalDate

class DlgProdutoKardec(val viewModel: TabEstoqueSaldoViewModel, val produto: ProdutoEstoque) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoKardec::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val codigo = produto.codigo ?: 0
    val descricao = produto.descricao ?: ""
    val grade = produto.grade.let {gd ->
      if(gd.isNullOrBlank()) "" else " - $gd"
    }
    val locApp = produto.locApp
    val locSaci = produto.locSaci

    val localizacao = if(locApp.isNullOrBlank()) locSaci ?: "" else locApp
    val dataInicial = produto.dataInicial ?: LocalDate.now().withDayOfMonth(1)

    form = SubWindowForm("$codigo $descricao$grade ($localizacao)\nData Inicial: ${dataInicial.format()}", toolBar = {
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
      setSelectionMode(Grid.SelectionMode.MULTI)

      columnGrid(ProdutoKardec::loja, "Loja")
      columnGrid(ProdutoKardec::data, "Data")
      columnGrid(ProdutoKardec::doc, "Doc")
      columnGrid(ProdutoKardec::tipo, "Tipo")
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
    val kardec = viewModel.kardec(produto)
    gridDetail.setItems(kardec)
  }

  fun close() {
    onClose?.invoke()
    form?.close()
  }
}