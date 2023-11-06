package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.layout.AppLayoutAbstract
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.notaEntrada.NotaEntradaView
import br.com.astrosoft.produto.view.notaSaida.NotaView
import br.com.astrosoft.produto.view.pedido.PedidoView
import br.com.astrosoft.produto.view.produto.ProdutoView
import br.com.astrosoft.produto.view.ressuprimento.RessuprimentoView
import com.github.mvysny.karibudsl.v23.route
import com.github.mvysny.karibudsl.v23.sideNav
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.icon.VaadinIcon.*
import com.vaadin.flow.component.tabs.Tabs

class ProdutoLayout : AppLayoutAbstract() {
  override fun HasComponents.navigation() {
    sideNav {
      val userSaci = AppConfig.userLogin() as? UserSaci
      if (userSaci?.produto == true) route(icon = FORM, title = "Produtos", routeClass = ProdutoView::class)
      if (userSaci?.nota == true) route(icon = DIPLOMA, title = "Notas de Saída", routeClass = NotaView::class)
      if (userSaci?.notaEntrada == true) route(
        icon = DIPLOMA,
        title = "Notas de Entrada",
        routeClass = NotaEntradaView::class
      )
      if (userSaci?.pedido == true) route(icon = DIPLOMA, title = "Pedido", routeClass = PedidoView::class)
      if (userSaci?.pedido == true) route(
        icon = DIPLOMA,
        title = "Ressuprimento",
        routeClass = RessuprimentoView::class
      )
      if (userSaci?.admin == true) route(icon = USER, title = "Usuário", routeClass = UsuarioView::class)
    }
  }
}
