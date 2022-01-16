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
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFUsuarioNameCD
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFUsuarioNameExp
import br.com.astrosoft.produto.viewmodel.nota.TabNotaEntViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosEnt(val viewModel: TabNotaEntViewModel, val nota: NotaSaida) {
  private val gridDetail = Grid(ProdutoNF::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val form = SubWindowForm("Produtos da nota ${nota.nota} loja: ${nota.loja}", toolBar = {
      button("Volta") {
        val user = Config.user as? UserSaci
        isVisible = user?.voltarEnt == true || user?.admin == true
        icon = VaadinIcon.ARROW_LEFT.create()
        onLeftClick {
          viewModel.marcaCD()
          gridDetail.setItems(nota.produtos(EMarcaNota.ENT))
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
    form.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)
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
    val listProdutos = nota.produtos(EMarcaNota.ENT)
    gridDetail.setItems(listProdutos)
  }
}