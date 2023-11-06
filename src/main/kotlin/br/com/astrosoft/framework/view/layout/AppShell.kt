package br.com.astrosoft.framework.view.layout

import br.com.astrosoft.framework.model.config.AppConfig
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.server.AppShellSettings
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo

@Theme("my-theme", variant = Lumo.DARK)
@Push
@NpmPackage(value = "line-awesome", version = "1.3.0")
@Viewport(Viewport.DEVICE_DIMENSIONS)
class AppShell : AppShellConfigurator {
  init {
    this.configurePage(AppSetting())
  }
}

class AppSetting : AppShellSettings() {
  init {
    this.addFavIcon("icon", AppConfig.iconPath, "192x192")
    this.setPageTitle(AppConfig.title)
  }
}
