package br.com.astrosoft.framework.view.config

import com.vaadin.flow.component.Component
import kotlin.reflect.KClass

interface IRouteMainProvider {
  val routeMain: KClass<out Component>
}