package br.com.astrosoft.framework.view.layout

import br.com.astrosoft.framework.model.config.AppConfig
import com.github.mvysny.vaadinboot.VaadinBoot

fun main() {
  val context = AppConfig.context
  val boot = VaadinBoot()
    .withContextRoot("/$context")
    .openBrowserInDevMode(false)
    .disableClasspathScanning(false)
  boot.listenOn = null
  boot.run()
}
