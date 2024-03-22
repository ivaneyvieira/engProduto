package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

abstract class TabPanelUser(val viewModel: TabUsrViewModel) : TabPanelGrid<UserSaci>(UserSaci::class) {
  abstract fun Grid<UserSaci>.configGrid()
  override fun Grid<UserSaci>.gridPanel() {
    this.format()

    columnGrid(UserSaci::no, "Código")
    columnGrid(UserSaci::login, "Login")
    columnGrid(UserSaci::name, "Nome")

    this.configGrid()
  }

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

  override fun isAuthorized(): Boolean {
    val user = AppConfig.userLogin()
    return user?.admin == true
  }

  override val label: String
    get() = "Usuários"

  override fun updateComponent() {
    viewModel.updateView()
  }

  fun updateUsuarios(usuarios: List<UserSaci>) {
    updateGrid(usuarios)
  }

  fun selectedItem(): UserSaci? {
    return itensSelecionados().firstOrNull()
  }

  abstract fun FormUsuario.configFields()

  private fun FormUsuario.configFieldsDefault(isReadOnly: Boolean) {
    textField("Login do Usuário") {
      this.isReadOnly = isReadOnly
      this.width = "300px"
      binder.bind(this, UserSaci::login.name)
    }
    textField("Nome do Usuário") {
      this.isReadOnly = isReadOnly
      this.width = "300px"
      this.isReadOnly = true
      binder.bind(this, UserSaci::name.name)
    }
  }

  fun formUpdUsuario(usuario: UserSaci) {
    val form = FormUsuario(usuario) {
      this.configFieldsDefault(true)
      this.configFields()
    }
    DialogHelper.showForm(caption = "Usuário", form = form) {
      viewModel.updUser(form.userSaci)
    }
  }

  fun formAddUsuario() {
    val form = FormUsuario(UserSaci()) {
      this.configFieldsDefault(false)
      this.configFields()
    }
    DialogHelper.showForm(caption = "Usuário", form = form) {
      viewModel.addUser(form.userSaci)
    }
  }
}