package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.produto.model.beans.EMarcaRessuprimento
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.Ressuprimento
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoBarcode
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoCodigo
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoDescricao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoEstoque
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoGrade
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoLocalizacao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoQuantidade
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoEntViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosRessuEnt(val viewModel: TabRessuprimentoEntViewModel, val ressuprimento: Ressuprimento) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoRessuprimento::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos do ressuprimento ${ressuprimento.numero}", toolBar = {
      button("Volta") {
        val user = AppConfig.userLogin() as? UserSaci
        isVisible = user?.voltarEnt == true || user?.admin == true
        icon = VaadinIcon.ARROW_LEFT.create()
        onLeftClick {
          viewModel.marcaCD()
          gridDetail.setItems(ressuprimento.produtos(EMarcaRessuprimento.ENT))
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
      produtoRessuprimentoCodigo()
      produtoRessuprimentoBarcode()
      produtoRessuprimentoDescricao()
      produtoRessuprimentoGrade()
      produtoRessuprimentoLocalizacao()
      produtoRessuprimentoQuantidade()
      produtoRessuprimentoEstoque()
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoRessuprimento> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = ressuprimento.produtos(EMarcaRessuprimento.ENT)
    gridDetail.setItems(listProdutos)
  }
}