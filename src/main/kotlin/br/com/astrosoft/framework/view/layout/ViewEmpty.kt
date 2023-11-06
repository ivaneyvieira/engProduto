package br.com.astrosoft.framework.view.layout

import br.com.astrosoft.framework.model.config.AppConfig
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route("")
@PermitAll
class ViewEmpty : VerticalLayout(), BeforeEnterObserver {
  override fun beforeEnter(event: BeforeEnterEvent?) {
    if (event?.navigationTarget == ViewEmpty::class.java) event.forwardTo(AppConfig.routeMain().java)
  }
}