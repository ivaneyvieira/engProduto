package br.com.astrosoft.produto.model

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.security.IFindUser
import br.com.astrosoft.produto.model.beans.UserSaci

class FindUser : IFindUser {
  override fun findUser(username: String, senha: String): IUser? {
    return UserSaci.findUser(username).firstOrNull { user ->
      user.senha.trim() == senha.trim()
    }
  }
}