package br.com.astrosoft.framework.view.layout

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.security.LoginService
import br.com.astrosoft.framework.view.config.ViewUtil
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.applayout.AppLayout

abstract class AppLayoutAbstract : AppLayout() {
  init {
    isDrawerOpened = false
    navbar {
      drawerToggle()
      h3(AppConfig.title)
      horizontalLayout {
        isExpand = true
      }
      button("Sair") {
        onClick {
          LoginService.get().logout()
        }
      }
    }
    drawer {
      verticalLayout {
        isMargin = false
        isSpacing = false
        isPadding = false
        nativeLabel("Versão ${ViewUtil.versao}")
        nativeLabel(AppConfig.userLogin()?.login)
      }
      hr()
      this.navigation()
    }
  }

  abstract fun HasComponents.navigation()
}