package br.com.astrosoft.produto.viewmodel

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.IUsuarioView
import br.com.astrosoft.framework.viewmodel.UserViewModel
import br.com.astrosoft.produto.model.beans.Impressora
import br.com.astrosoft.produto.model.beans.Local
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci

class UsuarioViewModel(view: IUsuarioView) : UserViewModel<UserSaci, IUsuarioView>(view) {
  override fun listTab(): List<ITabView> = emptyList()

  override fun findAllUser() = UserSaci.findAll()

  override fun findUser(user: UserSaci) = UserSaci.findUser(user.login).firstOrNull()
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

  fun allImpressoras() = Impressora.allTermica()

  override fun createNew(): UserSaci {
    return UserSaci()
  }

  fun allLocais(): List<Local> = Local.all()
}

