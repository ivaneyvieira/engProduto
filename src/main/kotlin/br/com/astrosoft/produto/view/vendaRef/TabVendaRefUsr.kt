package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.vendaRef.ITabVendaRefUsr
import br.com.astrosoft.produto.viewmodel.vendaRef.VendaRefUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabVendaRefUsr(viewModel: VendaRefUsrViewModel) : TabPanelUser(viewModel), ITabVendaRefUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::tabVendaRef, "Vendas")
    columnGrid(UserSaci::tabResumo, "Resumo")
    columnGrid(UserSaci::tabResumoPgto, "Resumo Pgto")
  }

  override fun FormUsuario.configFields() {
    horizontalBlock {
      verticalBlock("Menu") {
        checkBox("Vendas") {
          binder.bind(this, UserSaci::tabVendaRef.name)
        }
        checkBox("Resumo") {
          binder.bind(this, UserSaci::tabResumo.name)
        }
        checkBox("Resumo Pgto") {
          binder.bind(this, UserSaci::tabResumoPgto.name)
        }
      }
    }
  }
}
