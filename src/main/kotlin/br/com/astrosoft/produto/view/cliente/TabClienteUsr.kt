package br.com.astrosoft.produto.view.cliente

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.cliente.ITabClienteUsr
import br.com.astrosoft.produto.viewmodel.cliente.TabClienteUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabClienteUsr(viewModel: TabClienteUsrViewModel) : TabPanelUser(viewModel), ITabClienteUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::clienteCadastro, "Cadastro")
  }

  override fun FormUsuario.configFields() {
    horizontalBlock {
      verticalBlock("Menu") {
        checkBox("Cadastro") {
          binder.bind(this, UserSaci::clienteCadastro.name)
        }
      }
      verticalBlock("Comandos") {
      }
    }
    verticalBlock("Filtros") {
      filtroImpressoraTermica(binder, UserSaci::impressoraDev)
      filtroLoja(binder, UserSaci::lojaVale)
    }
  }
}
