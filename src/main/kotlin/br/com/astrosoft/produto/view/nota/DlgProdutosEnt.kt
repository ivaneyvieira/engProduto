package br.com.astrosoft.produto.view.nota

import br.com.astrosoft.framework.view.SubWindowForm
import br.com.astrosoft.produto.model.beans.EMarcaNota
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.ProdutoNF
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFGradeAlternativa
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFUsuario
import br.com.astrosoft.produto.viewmodel.nota.TabNotaEntViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosEnt(val viewModel: TabNotaEntViewModel) {
  private val gridDetail = Grid(ProdutoNF::class.java, false)
  fun showDialog(nota: NotaSaida) {
    val listProdutos = nota.produtos(EMarcaNota.ENT)

    val form = SubWindowForm("Produtos da nota ${nota.nota} loja: ${nota.loja}", toolBar = {
      button("Volta") {
        icon = VaadinIcon.ARROW_LEFT.create()
        onLeftClick {
          viewModel.desmarcaEnt()
          gridDetail.setItems(nota.produtos(EMarcaNota.ENT))
        }
      }
    }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos(listProdutos)
      }
    }
    form.open()
  }

  private fun HorizontalLayout.createGridProdutos(listPedidos: List<ProdutoNF>) {
    gridDetail.apply {
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)
      setItems(listPedidos)
      produtoNFUsuario()
      produtoNFCodigo()
      produtoNFDescricao()
      produtoNFGrade()
      produtoNFGradeAlternativa()
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFPrecoUnitario()
      produtoNFPrecoTotal()
    }
    this.addAndExpand(gridDetail)
  }

  fun itensSelecionados(): List<ProdutoNF> {
    return gridDetail.selectedItems.toList()
  }
}