package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.reposicao.ITabReposicaoUsr
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabReposicaoUsr(viewModel: TabReposicaoUsrViewModel) : TabPanelUser(viewModel), ITabReposicaoUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::reposicaoSep, "Separar")
    columnGrid(UserSaci::reposicaoEnt, "Entregar")
    columnGrid(UserSaci::impressoraRepo, "Impressora")
    columnGrid(UserSaci::localizacaoRepo, "Localização")
  }

  override fun FormUsuario.configFields() {
    verticalBlock("Menu") {
      checkBox("Separar") {
        binder.bind(this, UserSaci::reposicaoSep.name)
      }
      checkBox("Entregar") {
        binder.bind(this, UserSaci::reposicaoEnt.name)
      }
    }
    verticalBlock("Filtros") {
      filtroImpressoraTodas(binder, UserSaci::impressoraRepo)
      filtroLocalizacao(binder, UserSaci::localizacaoRepo)
    }
  }
}