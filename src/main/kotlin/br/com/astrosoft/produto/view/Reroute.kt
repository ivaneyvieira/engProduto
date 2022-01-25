package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.model.Config.user
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.notaSaida.NotaView
import br.com.astrosoft.produto.view.pedido.PedidoView
import br.com.astrosoft.produto.view.produto.ProdutoView
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.Route

@Route("", layout = ProdutoLayout::class)
class Reroute : Div(), BeforeEnterObserver {
  override fun beforeEnter(event: BeforeEnterEvent?) {
    val userSaci = user as? UserSaci
    when {
      userSaci?.produto == true -> event?.rerouteTo(ProdutoView::class.java)
      userSaci?.nota == true -> event?.rerouteTo(NotaView::class.java)
      userSaci?.pedido == true -> event?.rerouteTo(PedidoView::class.java)
      else -> Notification.show("Nenhuma rota encontrada")
    }
  }
}