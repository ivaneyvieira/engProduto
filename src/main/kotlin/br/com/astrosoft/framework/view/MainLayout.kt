package br.com.astrosoft.framework.view

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.session.LoginView
import br.com.astrosoft.framework.session.SecurityUtils
import br.com.astrosoft.framework.session.Session
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.RouterLayout
import kotlin.reflect.KClass

abstract class MainLayout : AppLayout(), RouterLayout, BeforeEnterObserver {
  init {
    isDrawerOpened = false
    navbar {
      drawerToggle()
      h3(Config.title)
      horizontalLayout {
        isExpand = true
      }
      button("Sair") {
        onLeftClick {
          Session.current.close()
          ui.ifPresent {
            it.session.close()
            it.navigate("")
          }
        }
      }
    }
    drawer {
      verticalLayout {
        label("Versão ${Config.version}")
        label(Config.user?.login)
      }
      hr()

      tabs {
        orientation = Tabs.Orientation.VERTICAL
        this.menuConfig()
      }
    }
  }

  abstract fun Tabs.menuConfig()

  override fun beforeEnter(event: BeforeEnterEvent) {
    if (!SecurityUtils.isUserLoggedIn) {
      event.rerouteTo(LoginView::class.java)
    }
  }

  fun Tabs.menuRoute(icon: VaadinIcon, text: String, viewType: KClass<out Component>, isEnabled: Boolean = true): Tab {
    return tab {
      this.isEnabled = isEnabled
      this.icon(icon)
      routerLink(text = text, viewType = viewType)
    }
  }/*
   fun configurePage(settings: InitialPageSettings?) {
    val attributes = HashMap<String, String>()
    attributes["rel"] = "shortcut icon"
    settings?.addLink(Config.iconPath, attributes)
  }

 */
}