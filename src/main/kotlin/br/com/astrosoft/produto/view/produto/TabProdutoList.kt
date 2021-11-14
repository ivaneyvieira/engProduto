package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.produto.model.beans.Produto
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoList
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoListViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabProdutoList(val viewModel: TabProdutoListViewModel) : TabPanelGrid<Produto>(Produto::class), ITabProdutoList {
  private lateinit var edtFiltro: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtFiltro = textField("Filtro") {
      width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<Produto>.gridPanel() { //setSelectionMode(MULTI)
    /**
    addColumnButton(FILE_TABLE, "Notas", "Notas") { fornecedor ->
    DlgNota(viewModel).showDialogNota(fornecedor) {
    viewModel.updateView()
    }
    }
     */

    /***
     * Colunas
     */
  }

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.produtoList == true
  }

  override val label: String
    get() = "Produto"

  override fun updateComponent() {
    viewModel.updateView()
  }
}
