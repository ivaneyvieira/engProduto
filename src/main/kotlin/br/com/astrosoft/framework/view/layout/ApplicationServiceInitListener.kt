package br.com.astrosoft.framework.view.layout

import br.com.astrosoft.framework.model.security.LoginService
import br.com.astrosoft.framework.view.security.LoginRoute
import com.github.mvysny.vaadinsimplesecurity.SimpleViewAccessChecker
import com.vaadin.flow.function.SerializableSupplier
import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.UIInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener

class LoginServiceSupplier : SerializableSupplier<LoginService> {
  override fun get(): LoginService {
    return LoginService.get()
  }
}

class ApplicationServiceInitListener : VaadinServiceInitListener {
  private val accessChecker = SimpleViewAccessChecker.usingService(LoginServiceSupplier())

  init {
    accessChecker.setLoginView(LoginRoute::class.java)
  }

  override fun serviceInit(event: ServiceInitEvent) {
    event.source.addUIInitListener { e: UIInitEvent ->
      e.ui.addBeforeEnterListener(accessChecker)
    }
  }
}