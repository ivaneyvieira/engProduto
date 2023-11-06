package br.com.astrosoft.framework.model.security

import br.com.astrosoft.framework.model.config.AppConfig
import com.github.mvysny.vaadinsimplesecurity.HasPassword
import java.io.Serializable

data class UserLogin(val id: Long, val username: String, private var hashedPassword: String, val roles: String) :
  HasPassword, Serializable {
  val roleSet: Set<String>
    get() {
      return roles.split(",").dropLastWhile { it.isEmpty() }.toSet()
    }

  class UserDao {
    fun findByUsername(username: String, senha: String): UserLogin? {
      val optUser = AppConfig.findUser(username, senha).let {
        if (it?.senha == senha) it
        else null
      }

      return optUser?.let { u ->
        UserLogin(
          id = u.no.toLong(),
          username = u.login,
          hashedPassword = u.senha,
          roles = ""
        )
      }
    }
  }

  companion object {
    val dao = UserDao()
  }

  override fun getHashedPassword(): String {
    return hashedPassword
  }

  override fun setHashedPassword(hashedPassword: String) {
    this.hashedPassword = hashedPassword
  }
}