package br.com.astrosoft.produto.model

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.IUserUtil
import br.com.astrosoft.produto.model.saci

class UserUtilImpl : IUserUtil {
  override fun findUser(username: String): IUser? = saci.findUser(username)
}