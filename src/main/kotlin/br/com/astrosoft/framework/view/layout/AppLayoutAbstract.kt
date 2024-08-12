package br.com.astrosoft.framework.view.layout

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.security.LoginService
import br.com.astrosoft.framework.view.config.ViewUtil
import br.com.astrosoft.framework.view.vaadin.ViewThread
import br.com.astrosoft.framework.viewmodel.ViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.progressbar.ProgressBar

abstract class AppLayoutAbstract : AppLayout() {
  private var loadIndicator: ProgressBar? = null

  init {
    isDrawerOpened = false
    navbar {
      drawerToggle()
      h3(AppConfig.title)
      horizontalLayout {
        width ="50px"
        isExpand = true
      }
      loadIndicator =progressBar {
        isIndeterminate = true
        this.isVisible = false
        ViewThread.registrerLoad(this)
      }
      horizontalLayout {
        width ="50px"
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