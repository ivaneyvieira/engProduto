package br.com.astrosoft.framework.view.layout

import jakarta.servlet.ServletContextEvent
import jakarta.servlet.ServletContextListener
import jakarta.servlet.annotation.WebListener
import java.util.*

@WebListener
class Bootstrap : ServletContextListener {
  override fun contextInitialized(sce: ServletContextEvent) {
    println("Starting up")

    Locale.setDefault(Locale("pt", "BR"))
    println("Starting up")
    val home = System.getenv("HOME")
    val fileName = System.getenv("EBEAN_PROPS") ?: "$home/ebean.properties"
    System.setProperty("ebean.props.file", fileName)
    println("##################### $fileName")
    println("Initialization complete")
  }
}