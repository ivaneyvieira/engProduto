package br.com.astrosoft.produto.viewmodel

import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.IUsuarioView
import br.com.astrosoft.framework.viewmodel.UserViewModel

class UsuarioViewModel(view: IUsuarioView) : UserViewModel<UserSaci, IUsuarioView>(view) {
  override fun listTab(): List<ITabView> = emptyList()

  override fun findAllUser() = UserSaci.findAll()

  override fun findUser(login: String) = UserSaci.findUser(login)

  override fun addUser(user: UserSaci) {
    UserSaci.updateUser(user)
  }

  override fun updateUser(user: UserSaci) {
    UserSaci.updateUser(user)
  }

  override fun deleteUser(user: UserSaci) {
    UserSaci.updateUser(user)
  }

  fun allLojas() = Loja.allLojas()
}

