package br.com.astrosoft.framework.view

import br.com.astrosoft.produto.model.beans.UserSaci
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.data.binder.Binder

class FormUsuario(userSaci: UserSaci, val init: FormUsuario.() -> Unit) : FormLayout() {
  val binder: Binder<UserSaci> = Binder(UserSaci::class.java)

  init {
    binder.bean = userSaci
    init()
  }

  val userSaci: UserSaci
    get() = binder.bean
}