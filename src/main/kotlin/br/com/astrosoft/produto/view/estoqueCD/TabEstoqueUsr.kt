package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueUsr
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.grid.Grid

class TabEstoqueUsr(viewModel: TabEstoqueUsrViewModel) : TabPanelUser(viewModel), ITabEstoqueUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::estoqueMF, "Estoque")
    columnGrid(UserSaci::estoqueCad, "Cad Loc")
    columnGrid(UserSaci::estoqueCD1A, "CD1A")
    columnGrid(UserSaci::localEstoque, "Localização")
  }

  override fun FormUsuario.configFields() {
    checkBox("Estoque") {
      binder.bind(this, UserSaci::estoqueMF.name)
    }
    checkBox("Cad Loc") {
      binder.bind(this, UserSaci::estoqueCad.name)
    }
    checkBox("CD1A") {
      binder.bind(this, UserSaci::estoqueCD1A.name)
    }
    select<String>("Localização") {
      setItems(listOf("TODOS") + viewModel.allLocalizacao())
      binder.bind(this, UserSaci::localEstoque.name)
    }
  }
}