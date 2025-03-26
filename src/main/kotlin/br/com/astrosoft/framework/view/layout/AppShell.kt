package br.com.astrosoft.framework.view.layout

import br.com.astrosoft.framework.model.config.AppConfig
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.component.page.*
import com.vaadin.flow.server.AppShellSettings
import com.vaadin.flow.server.PWA
import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo

@Theme("my-theme", variant = Lumo.DARK)
@Push
@NpmPackage(value = "line-awesome", version = "1.3.0")
@Viewport(Viewport.DEVICE_DIMENSIONS)
@BodySize(width = "100vw", height = "100vh")
@PWA(
  name = "Estoque Engecopi", shortName = "Estoque"
)
class AppShell : AppShellConfigurator, VaadinServiceInitListener {
  init {
    this.configurePage(AppSetting())
  }

  override fun serviceInit(event: ServiceInitEvent?) {
    event?.source?.addUIInitListener { uiInitEvent ->
      val conf: LoadingIndicatorConfiguration = uiInitEvent.ui.loadingIndicatorConfiguration
      // disable default theme -> loading indicator isn't shown
      conf.isApplyDefaultTheme = false

      /*
       * Delay for showing the indicator and setting the 'first' class name.
       */
      conf.firstDelay = 300 // 300ms is the default

      /* Delay for setting the 'second' class name */
      conf.secondDelay = 1500 // 1500ms is the default

      /* Delay for setting the 'third' class name */
      conf.thirdDelay = 5000 // 5000ms is the default
    }
  }
}

class AppSetting : AppShellSettings() {
  init {
    this.addFavIcon("icon", AppConfig.iconPath, "192x192")
    this.setPageTitle(AppConfig.title)
  }
}
