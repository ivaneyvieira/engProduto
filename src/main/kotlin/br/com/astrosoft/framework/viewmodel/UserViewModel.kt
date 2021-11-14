package br.com.astrosoft.framework.viewmodel

import br.com.astrosoft.framework.model.IUser

abstract class UserViewModel<B : IUser, V : IUsuarioView>(view: V) : ViewModel<V>(view) {
  abstract fun findAllUser(): List<B>
  abstract fun findUser(login: String): B?
  abstract fun addUser(user: B)
  abstract fun updateUser(user: B)
  abstract fun deleteUser(user: B)

  fun findAll(): List<B> {
    return findAllUser()
  }

  fun listLogins() = findAll().map {
    it.login
  }

  fun add(user: B?): B? {
    exec {
      user ?: fail("Usuário não selecionado")
      validaUser(user)
      user.ativo = true
      addUser(user)
    }
    return user
  }

  private fun validaUser(user: B?): B {
    user ?: fail("Usuário não selecionado")
    findUser(user.login) ?: fail("Usuário não encontrado no saci")
    return user
  }

  fun update(user: B?): B? {
    exec {
      updateUser(validaUser(user))
    }
    return user
  }

  fun delete(user: B?) {
    exec {
      user ?: fail("Usuário não selecionado")
      val userValid = validaUser(user)
      userValid.ativo = false
      deleteUser(userValid)
    }
  }
}

interface IUsuarioView : IView