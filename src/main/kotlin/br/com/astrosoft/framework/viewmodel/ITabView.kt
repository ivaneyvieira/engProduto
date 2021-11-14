package br.com.astrosoft.framework.viewmodel

import br.com.astrosoft.framework.model.IUser

interface ITabView {
  fun isAuthorized(user: IUser): Boolean
  val label: String
  fun updateComponent()
}
