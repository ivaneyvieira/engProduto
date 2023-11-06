package br.com.astrosoft.framework.viewmodel

interface ITabView {
  fun isAuthorized(): Boolean
  val label: String
  fun updateComponent()
}
