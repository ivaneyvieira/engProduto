package br.com.astrosoft.produto.model

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig.appName
import br.com.astrosoft.framework.model.security.IFindUser

class FindUser : IFindUser {
  override fun findUser(username: String, senha: String): IUser? {
    return saci.findUser(username).firstOrNull { user ->
      user.senha == senha
    }
  }
}