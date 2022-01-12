package br.com.astrosoft.produto.view.nota

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.view.SubWindowForm
import br.com.astrosoft.produto.model.beans.EMarcaNota
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.ProdutoNF
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFBarcode
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFGradeAlternativa
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFUsuarioNameExp
import br.com.astrosoft.produto.viewmodel.nota.TabNotaCDViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosCD(val viewModel: TabNotaCDViewModel, val nota: NotaSaida) {
  private val gridDetail = Grid(ProdutoNF::class.java, false)
  fun showDialog() {
    val form = SubWindowForm("Produtos da nota ${nota.nota} loja: ${nota.loja}", toolBar = {
      button("Volta") {
        val user = Config.user as? UserSaci
        isVisible = user?.voltarCD == true || user?.admin == true
        icon = VaadinIcon.ARROW_LEFT.create()
        onLeftClick {
          viewModel.marcaExp()
        }
      }
      textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        addValueChangeListener {
          if(it.isFromClient) {
            viewModel.marcaEntProdutos(it.value)
            this@textField.value = ""
            this@textField.focus()
          }
        }
      }
    }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)
      produtoNFUsuarioNameExp()
      produtoNFCodigo()
      produtoNFBarcode()
      produtoNFDescricao()
      produtoNFGrade()
      produtoNFGradeAlternativa()
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFPrecoUnitario()
      produtoNFPrecoTotal()
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoNF> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos(EMarcaNota.CD)
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): ProdutoNF? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { it.barcode == codigoBarra }
  }
}