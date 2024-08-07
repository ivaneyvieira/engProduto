package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.recebimento.ITabRecebimentoUsr
import br.com.astrosoft.produto.viewmodel.recebimento.TabRecebimentoUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabRecebimentoUsr(viewModel: TabRecebimentoUsrViewModel) : TabPanelUser(viewModel), ITabRecebimentoUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::recebimentoReceber, "Receber")
    columnGrid(UserSaci::recebimentoRecebido, "Recebido")
  }

  override fun FormUsuario.configFields() {
    verticalBlock("Menu") {
      checkBox("Receber") {
        binder.bind(this, UserSaci::recebimentoReceber.name)
      }
      checkBox("Recebido") {
        binder.bind(this, UserSaci::recebimentoRecebido.name)
      }
      checkBox("Cadastra Validade") {
        binder.bind(this, UserSaci::recebimentoCadastraValidade.name)
      }
    }
    verticalBlock("Filtros") {
      filtroLoja(binder, UserSaci::lojaRec)
      filtroLocalizacao(binder, UserSaci::localizacaoRec)
    }
  }
}