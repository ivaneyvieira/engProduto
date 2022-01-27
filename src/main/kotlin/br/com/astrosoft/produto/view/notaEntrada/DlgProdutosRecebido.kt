package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.view.SubWindowForm
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEBarcode
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFECodigo
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEDescricao
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEGrade
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEQuantidade
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaRecebidoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.selectAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosRecebido(val viewModel: TabNotaEntradaRecebidoViewModel, val nota: NotaEntrada) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFE::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos da Nota de Entrada ${nota.nota} loja ${nota.loja}", toolBar = {
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
      produtoNFEQuantidade()
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun update() {
    val listProdutos = viewModel.produtos()
    gridDetail.setItems(listProdutos)
  }
}