package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.config.IRouteMainProvider
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.pedidoTransf.PedidoTransfView
import br.com.astrosoft.produto.view.produto.ProdutoView
import br.com.astrosoft.produto.view.ressuprimento.RessuprimentoView
import com.vaadin.flow.component.Component
import kotlin.reflect.KClass

class RouteMainProvider : IRouteMainProvider {
  override val routeMain: KClass<out Component>
    get() {
      val userSaci = AppConfig.userLogin() as? UserSaci

      return when {
        userSaci?.pedidoTransf == true -> PedidoTransfView::class
        userSaci?.pedido == true -> RessuprimentoView::class
        userSaci?.admin == true -> UsuarioView::class
        else -> ProdutoView::class
      }
    }
}