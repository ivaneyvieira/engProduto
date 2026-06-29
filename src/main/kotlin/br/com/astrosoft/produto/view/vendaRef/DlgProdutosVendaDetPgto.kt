package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.list
import br.com.astrosoft.produto.model.beans.NotaVendaDet
import br.com.astrosoft.produto.model.beans.ParcelasVenda
import br.com.astrosoft.produto.viewmodel.vendaRef.TabVendaDetViewModel
import com.github.mvysny.kaributools.asc
import com.github.mvysny.kaributools.sort
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosVendaDetPgto(val viewModel: TabVendaDetViewModel, val nota: NotaVendaDet) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ParcelasVenda::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    val readOnly = false
    val espaco = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0"
    val nomeCliente = if (nota.nomeCliente.isNullOrBlank())
      "NÃO INFORMADO"
    else
      nota.nomeCliente
    val linha1 =
        "Loja: ${nota.loja.format("00")}${espaco}NF: ${nota.nota}${espaco}Data: ${nota.data.format()}${espaco}Vendedor: ${nota.vendedor}"
    val linha2 =
        "Tipo NF: ${nota.tipoNf}${espaco}${espaco}Cliente: ${nota.cliente} - $nomeCliente"
    form = SubWindowForm(
      title = "$linha1|$linha2",
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

      this.columnGrid(ParcelasVenda::transacao, "Transação")
      this.columnGrid(ParcelasVenda::tipo, "Tipo")
      this.columnGrid(ParcelasVenda::dataVenda, "Data")
      this.columnGrid(ParcelasVenda::documento, "Documento")
      this.columnGrid(ParcelasVenda::valorParcela, "Valor")
      this.columnGrid(ParcelasVenda::dataParcela, "Vencto")

      this.sort(ParcelasVenda::dataParcela.asc)
    }
    this.addAndExpand(gridDetail)

    update()
  }

  fun update() {
    val listParcelas = nota.parcelas()
    gridDetail.setItems(listParcelas)
    gridDetail.recalculateColumnWidths()
  }

  fun parcelas(): List<ParcelasVenda> {
    return gridDetail.list()
  }

  fun fecha() {
    form?.close()
  }
}