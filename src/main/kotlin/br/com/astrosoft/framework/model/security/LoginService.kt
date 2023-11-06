package br.com.astrosoft.framework.model.security

import com.github.mvysny.vaadinsimplesecurity.AbstractLoginService
import com.github.mvysny.vaadinsimplesecurity.SimpleUserWithRoles
import javax.security.auth.login.FailedLoginException
import javax.security.auth.login.LoginException

class LoginService private constructor() : AbstractLoginService<UserLogin>() {
  @Throws(LoginException::class)
  fun login(username: String, password: String) {
    val user: UserLogin =
        UserLogin.dao.findByUsername(username, password)
        ?: throw FailedLoginException("Nome de usuário ou senha inválidos")
    if (user.hashedPassword != password) {
      throw FailedLoginException("Nome de usuário ou senha inválidos")
    }
    login(user)
  }

  override fun toUserWithRoles(user: UserLogin): SimpleUserWithRoles {
    return SimpleUserWithRoles(user.username, user.roleSet)
  }

  companion object {
    fun get(): LoginService {
      return get(LoginService::class.java) {
        LoginService()
      }
    }
  }
}