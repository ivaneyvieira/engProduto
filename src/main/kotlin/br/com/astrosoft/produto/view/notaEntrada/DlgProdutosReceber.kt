package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.view.SubWindowForm
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEBarcode
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFECodigo
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEDescricao
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEGrade
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEPrecoTotal
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEPrecoUnitario
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEQuantidade
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEQuantidadeRef
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaReceberViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.header2
import com.github.mvysny.kaributools.selectAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosReceber(val viewModel: TabNotaEntradaReceberViewModel, val nota: NotaEntrada) {
  private lateinit var edtQuant: IntegerField
  private lateinit var edtCodbar: TextField
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFE::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos da Nota de Entrada ${nota.nota} loja ${nota.loja}", toolBar = {
      edtCodbar = textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        this.isAutoselect = true
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.marcaProdutos(edtCodbar.value, edtQuant.value ?: 0)
            edtQuant.value = 0
            edtQuant.focus()
          }
        }
      }
      edtQuant = integerField("Quantidade") {
        this.isAutoselect = true
        this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.marcaProdutos(edtCodbar.value, edtQuant.value)
            edtCodbar.value = ""
            edtQuant.value = 0
            edtCodbar.selectAll()
            edtCodbar.focus()
          }
        }
      }
      button("Processa") {
        onLeftClick {
          viewModel.processaProdutos()
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

      addColumnButton(VaadinIcon.TRASH, "Remover", "Remover") { produto ->
        viewModel.removeProduto(produto)
      }
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

  fun produtosSelecionados(): List<ProdutoNFE> {
    return gridDetail.selectedItems.toList()
  }
}