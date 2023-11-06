package br.com.astrosoft.framework.model.security

import br.com.astrosoft.framework.model.IUser

interface IFindUser {
  fun findUser(username: String, senha: String): IUser?
}