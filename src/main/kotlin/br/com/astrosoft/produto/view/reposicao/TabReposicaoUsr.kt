package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.reposicao.ITabReposicaoUsr
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoUsrViewModel
import com.github.mvysny.karibudsl.v10.button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class TabReposicaoUsr(val viewModel: TabReposicaoUsrViewModel) :
  TabPanelGrid<UserSaci>(UserSaci::class), ITabReposicaoUsr {
  override fun HorizontalLayout.toolBarConfig() {
    button("Adicionar") {
      this.icon = VaadinIcon.PLUS.create()

      addClickListener {
        viewModel.adicionaUsuario()
      }
    }
    button("Atualizar") {
      this.icon = VaadinIcon.REFRESH.create()

      addClickListener {
        viewModel.modificarUsuario()
      }
    }
    button("Remove") {
      this.icon = VaadinIcon.TRASH.create()

      addClickListener {
        viewModel.removeUsuario()
      }
    }
  }

  override fun Grid<UserSaci>.gridPanel() {
    this.format()

    columnGrid(UserSaci::no, "Código")
    columnGrid(UserSaci::login, "Login")
    columnGrid(UserSaci::name, "Nome")
    columnGrid(UserSaci::impressoraRepo, "Impressora")
    columnGrid(UserSaci::localizacaoRepo, "Localização")
  }

  override fun updateUsuarios(usuarios: List<UserSaci>) {
    updateGrid(usuarios)
  }

  override fun formAddUsuario() {
    val form = FormUsuario(UserSaci())
    DialogHelper.showForm(caption = "Usuário", form = form) {
      viewModel.addUser(form.userSaci)
    }
  }

  override fun selectedItem(): UserSaci? {
    return itensSelecionados().firstOrNull()
  }

  override fun formUpdUsuario(usuario: UserSaci) {
    val form = FormUsuario(usuario)
    DialogHelper.showForm(caption = "Usuário", form = form) {
      viewModel.updUser(form.userSaci)
    }
  }

  override fun isAuthorized(): Boolean {
    val user = AppConfig.userLogin()
    return user?.admin == true
  }

  override val label: String
    get() = "Usuários"

  override fun updateComponent() {
    viewModel.updateView()
  }
}