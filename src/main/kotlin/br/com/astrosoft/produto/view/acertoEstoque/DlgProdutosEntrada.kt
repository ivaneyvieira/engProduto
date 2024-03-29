package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.AcertoEntradaNota
import br.com.astrosoft.produto.model.beans.AcertoEntradaProduto
import br.com.astrosoft.produto.viewmodel.acertoEstoque.TabAcertoEstoqueEntradaViewModel
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosEntrada(val viewModel: TabAcertoEstoqueEntradaViewModel, val nota: AcertoEntradaNota) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(AcertoEntradaProduto::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos da Nota ${nota.notaFiscal} loja: ${nota.loja}", toolBar = {
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
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS)
      isMultiSort = false

      columnGrid(AcertoEntradaProduto::codigoProduto, "Código")
      columnGrid(AcertoEntradaProduto::nomeProduto, "Descrição").expand()
      columnGrid(AcertoEntradaProduto::grade, "Grade")
      columnGrid(AcertoEntradaProduto::rotulo, "Rótulo")
      columnGrid(AcertoEntradaProduto::tributacao, "Tributação")
      columnGrid(AcertoEntradaProduto::quantidade, "Quant")
      columnGrid(AcertoEntradaProduto::valorUnitario, "V Unit")
      columnGrid(AcertoEntradaProduto::valorTotal, "V Total", width = "180px") {
        this.grid.dataProvider.addDataProviderListener {
          val total = nota.produtos.sumOf { it.valorTotal ?: 0.0 }
          setFooter(Html("<b><font size=4>Total R$ &nbsp;&nbsp;&nbsp;&nbsp; ${total.format()}</font></b>"))
        }
        val total = nota.produtos.sumOf { it.valorTotal ?: 0.0 }
        setFooter(Html("<b><font size=4>Total R$ &nbsp;&nbsp;&nbsp;&nbsp; ${total.format()}</font></b>"))
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<AcertoEntradaProduto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos
    gridDetail.setItems(listProdutos)
  }
}