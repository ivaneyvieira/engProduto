package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.layout.AppLayoutAbstract
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.devCliente.DevClienteView
import br.com.astrosoft.produto.view.pedidoTransf.PedidoTransfView
import br.com.astrosoft.produto.view.ressuprimento.RessuprimentoView
import com.github.mvysny.karibudsl.v23.route
import com.github.mvysny.karibudsl.v23.sideNav
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.icon.VaadinIcon.DIPLOMA
import com.vaadin.flow.component.icon.VaadinIcon.USER

class ProdutoLayout : AppLayoutAbstract() {
  override fun HasComponents.navigation() {
    sideNav {
      val userSaci = AppConfig.userLogin() as? UserSaci
      if (userSaci?.pedido == true) route(
        icon = DIPLOMA,
        title = "Ressuprimento",
        routeClass = RessuprimentoView::class
      )
      if (userSaci?.pedidoTransf == true) route(
        icon = DIPLOMA,
        title = "Pedido Transf",
        routeClass = PedidoTransfView::class
      )
      if (userSaci?.devCliente == true) route(
        icon = DIPLOMA,
        title = "Dev Cliente",
        routeClass = DevClienteView::class,
      )

      if (userSaci?.admin == true) route(icon = USER, title = "Usuário", routeClass = UsuarioView::class)
    }
  }
}
