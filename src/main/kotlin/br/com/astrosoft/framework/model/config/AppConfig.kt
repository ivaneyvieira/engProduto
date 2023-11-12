package br.com.astrosoft.framework.model.config

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.security.IFindUser
import br.com.astrosoft.framework.model.security.LoginService
import br.com.astrosoft.framework.view.config.IRouteMainProvider
import java.io.IOException
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object AppConfig {
  @Throws(IOException::class)
  private fun loadProperties(): Properties {
    val configuration = Properties()
    val inputStream = AppConfig::class.java
      .classLoader
      .getResourceAsStream("application.properties")
    configuration.load(inputStream)
    inputStream?.close()
    return configuration
  }

  private val properties by lazy { loadProperties() }

  val commpany = properties.getProperty("app.commpany")
  val title = properties.getProperty("app.title")
  val shortName = properties.getProperty("app.shortName")
  val iconPath = properties.getProperty("app.iconPath")
  val appName = properties.getProperty("app.appName")
  val context = properties.getProperty("app.context")

  private val findUserClass: KClass<IFindUser> = properties.getProperty("app.findUserClass").toClass()
  private val findUser by lazy { findUserClass.createInstance() }

  private val routeMainProviderClass: KClass<IRouteMainProvider> =
      properties.getProperty("app.routeMainProvider").toClass()
  private val routeMainProvider by lazy { routeMainProviderClass.createInstance() }
  fun routeMain() = routeMainProvider.routeMain

  val test: Boolean = properties.getProperty("app.test").toBoolean()

  fun userLogin(): IUser? {
    val currentUser = LoginService.get().currentUser
    return findUser(currentUser?.username, currentUser?.hashedPassword)
  }

  fun findUser(username: String?, senha: String?): IUser? {
    username ?: return null
    senha ?: return null
    return findUser.findUser(username, senha)
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T : Any> String.toClass(): KClass<T> {
    return Class.forName(this).kotlin as KClass<T>
  }

  private fun String.toBoolean(): Boolean {
    return this == "true"
  }
}
