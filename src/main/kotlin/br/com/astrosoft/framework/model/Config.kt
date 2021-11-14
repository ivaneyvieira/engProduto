package br.com.astrosoft.framework.model

import br.com.astrosoft.framework.session.SecurityUtils
import br.com.astrosoft.framework.util.SystemUtils
import com.vaadin.flow.component.Component
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.util.*
import kotlin.reflect.KClass

object Config {
  private fun properties(): Properties {
    val properties = Properties()
    val filename = "/application.properties"
    val resource = SystemUtils::class.java.getResource(filename) ?: throw IOException()
    val file = File(resource.file)
    return if (file.exists()) {
      properties.load(FileReader(file))
      properties
    }
    else throw FileNotFoundException("Arquivo de propriedade não encontrado")
  }

  private val prop = properties()

  val appName = prop.getProperty("appName")
  val commpany = prop.getProperty("commpany")
  val title = prop.getProperty("title")
  val shortName = prop.getProperty("shortName")
  val iconPath = prop.getProperty("iconPath")

  val version: String
    get() {
      val arquivo = "/versao.txt"
      return SystemUtils.readFile(arquivo)
    }
  val user get() = SecurityUtils.userDetails
  val isAdmin get() = user?.admin == true

  @Suppress("UNCHECKED_CAST")
  val mainClass: KClass<Component> = Class.forName(prop.getProperty("mainClass")).kotlin as KClass<Component>
  val userUtilImpl: IUserUtil = Class.forName(prop.getProperty("userUtilImpl")).getDeclaredConstructor().newInstance()
          as IUserUtil
}