package br.com.astrosoft.framework.view.layout

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.security.LoginService
import br.com.astrosoft.framework.view.config.ViewUtil
import br.com.astrosoft.framework.view.vaadin.helper.DlgAlteraSenha
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.applayout.AppLayout

abstract class AppLayoutAbstract : AppLayout() {
  init {
    isDrawerOpened = false
    navbar(true) {
      drawerToggle()
      h3(AppConfig.title)
      horizontalLayout {
        isExpand = true
        this.isMargin = false
        this.isPadding = false
      }
      horizontalLayout {
        this.isMargin = true
        this.isPadding = false
        this.isSpacing = true

        button("Senha") {
          onClick {
            DlgAlteraSenha().show()
          }
        }
        button("Sair") {
          onClick {
            LoginService.get().logout()
          }
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