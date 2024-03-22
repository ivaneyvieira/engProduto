package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.reposicao.ITabReposicaoUsr
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.data.binder.Binder

class TabReposicaoUsr(viewModel: TabReposicaoUsrViewModel) : TabPanelUser(viewModel), ITabReposicaoUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::reposicaoSep, "Separar")
    columnGrid(UserSaci::reposicaoEnt, "Entregar")
    columnGrid(UserSaci::impressoraRepo, "Impressora")
    columnGrid(UserSaci::localizacaoRepo, "Localização")
  }

  override fun FormUsuario.configFields() {
    checkBox("Separar") {
      binder.bind(this, UserSaci::reposicaoSep.name)
    }
    checkBox("Entregar") {
      binder.bind(this, UserSaci::reposicaoEnt.name)
    }
    multiSelectComboBox<String>("Localização") {
      setItems(listOf("TODOS") + viewModel.allLocalizacao())
      binder.bind(this, UserSaci::localizacaoRepo.name)
    }
    multiSelectComboBox<String>("Impressora") {
      this.isExpand = true
      setItems(listOf("TODAS") + viewModel.allImpressoras().map { it.name })
      binder.bind(this, UserSaci::impressoraRepo.name)
    }
  }
}