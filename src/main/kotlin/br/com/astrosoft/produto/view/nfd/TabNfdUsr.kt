package br.com.astrosoft.produto.view.nfd

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.nfd.ITabNfdUsr
import br.com.astrosoft.produto.viewmodel.nfd.TabNfdUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabNfdUsr(viewModel: TabNfdUsrViewModel) : TabPanelUser(viewModel), ITabNfdUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::nfdDevFor, "Exp")
  }

  override fun FormUsuario.configFields() {
    verticalBlock("Menus") {
      checkBox("Dev For") {
        binder.bind(this, UserSaci::nfdDevFor.name)
      }
    }
  }
}