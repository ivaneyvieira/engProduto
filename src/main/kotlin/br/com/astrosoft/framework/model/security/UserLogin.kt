package br.com.astrosoft.framework.model.security

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.produto.model.beans.UserSaci
import com.github.mvysny.vaadinsimplesecurity.HasPassword
import java.io.Serializable

data class UserLogin(val id: Long, val username: String, private var hashedPassword: String, val roles: String) :
    HasPassword, Serializable {
  val roleSet: Set<String>
    get() {
      return roles.split(",").dropLastWhile { it.isEmpty() }.toSet()
    }
  
  class UserDao(val findUser: (username: String, senha: String) -> IUser?) {
    fun findByUsername(username: String, senha: String): UserLogin? {
      val optUser = findUser(username, senha).let {
        if (it?.senha == senha) it
        else null
      }
      
      return optUser?.let { u ->
        UserLogin(
          id = u.no.toLong(), username = u.login ?: "", hashedPassword = u.senha ?: "", roles = ""
        )
      }
    }
  }
  
  companion object {
    fun dao() = UserDao { user, senha ->
      UserSaci.findUser(user).firstOrNull{
        it.senha == senha
      }
    }
  }
  
  override fun getHashedPassword(): String {
    return hashedPassword
  }
  
  override fun setHashedPassword(hashedPassword: String) {
    this.hashedPassword = hashedPassword
  }
}