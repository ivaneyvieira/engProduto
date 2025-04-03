package br.com.astrosoft.produto.view.devFor2

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.devFor2.ITabNotaUsr
import br.com.astrosoft.produto.viewmodel.devFor2.TabNotaUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabNotaUsr(viewModel: TabNotaUsrViewModel) : TabPanelUser(viewModel), ITabNotaUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::devFor2NotaPendencia, "Pendencia")
    columnGrid(UserSaci::devFor2NotaNFD, "NFD")
  }

  override fun FormUsuario.configFields() {
    horizontalBlock {
      verticalBlock("Menu") {
        checkBox("Pendencia") {
          binder.bind(this, UserSaci::devFor2NotaPendencia.name)
        }

        checkBox("NFD") {
          binder.bind(this, UserSaci::devFor2NotaNFD.name)
        }
      }
    }
  }
}
