package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.view.SubWindowForm
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEBarcode
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFECodigo
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEDescricao
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEGrade
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFELocalizacao
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEPrecoTotal
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEPrecoUnitario
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEQuantidade
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaReceberViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosReceber(val viewModel: TabNotaEntradaReceberViewModel, val nota: NotaEntrada) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFE::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos da Nota de Entrada ${nota.nota} loja ${nota.loja}", toolBar = {
      textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.marcaProdutos(it.value)
            this@textField.value = ""
            this@textField.focus()
          }
        }
      }
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
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)

      produtoNFECodigo()
      produtoNFEBarcode()
      produtoNFEDescricao()
      produtoNFEGrade()
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun update() {
    val listProdutos = nota.produtosConferencia()
    gridDetail.setItems(listProdutos)
  }
}