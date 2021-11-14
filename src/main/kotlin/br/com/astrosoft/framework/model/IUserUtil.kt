package br.com.astrosoft.framework.model

interface IUserUtil {
  fun findUser(username: String): IUser?
}
