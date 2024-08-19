package br.com.astrosoft.framework.view.layout

import br.com.astrosoft.framework.model.security.LoginService
import br.com.astrosoft.framework.view.security.LoginRoute
import com.github.mvysny.vaadinsimplesecurity.SimpleNavigationAccessControl
import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.UIInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener

class ApplicationServiceInitListener : VaadinServiceInitListener {
  private val accessControl = SimpleNavigationAccessControl.usingService(LoginService::get)

  init {
    accessControl.setLoginView(LoginRoute::class.java)
  }

  override fun serviceInit(event: ServiceInitEvent) {
    event.source.addUIInitListener { e: UIInitEvent ->
      e.ui.addBeforeEnterListener(accessControl)
    }
  }
}