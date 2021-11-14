package br.com.astrosoft.produto.view.fornecedor

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.produto.model.beans.Produto
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoList
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoListViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class TabProdutoList(viewModel: TabProdutoListViewModel) : TabPanelGrid<Produto>(Produto::class),
        ITabProdutoList {
  override fun HorizontalLayout.toolBarConfig() {
    TODO("Not yet implemented")
  }

  override fun Grid<Produto>.gridPanel() {
    TODO("Not yet implemented")
  }

  override fun isAuthorized(user: IUser): Boolean {
    TODO("Not yet implemented")
  }

  override val label: String
    get() = TODO("Not yet implemented")

  override fun updateComponent() {
    TODO("Not yet implemented")
  }
}
