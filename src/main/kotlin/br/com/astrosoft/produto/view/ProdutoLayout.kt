package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.layout.AppLayoutAbstract
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.acertoEstoque.AcertoEstoqueView
import br.com.astrosoft.produto.view.devCliente.DevClienteView
import br.com.astrosoft.produto.view.estoqueCD.EstoqueCDView
import br.com.astrosoft.produto.view.notaSaida.NotaView
import br.com.astrosoft.produto.view.pedidoTransf.PedidoTransfView
import br.com.astrosoft.produto.view.produto.ProdutoView
import br.com.astrosoft.produto.view.reposicao.ReposicaoView
import br.com.astrosoft.produto.view.ressuprimento.RessuprimentoView
import br.com.astrosoft.produto.view.retira.PedidoRetiraView
import com.github.mvysny.karibudsl.v23.route
import com.github.mvysny.karibudsl.v23.sideNav
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.icon.VaadinIcon.*

class ProdutoLayout : AppLayoutAbstract() {
  override fun HasComponents.navigation() {
    sideNav {
      val userSaci = AppConfig.userLogin() as? UserSaci
      if (userSaci?.ressuprimento == true) route(
        icon = SHOP,
        title = "Ressuprimento",
        routeClass = RessuprimentoView::class
      )
      if (userSaci?.nota == true) route(
        icon = OUT,
        title = "Expedição",
        routeClass = NotaView::class
      )
      if (userSaci?.reposicao == true) route(
        icon = SIGNAL,
        title = "Reposição Loja",
        routeClass = ReposicaoView::class
      )
      if (userSaci?.pedidoTransf == true) route(
        icon = EXCHANGE,
        title = "Pedido Transf",
        routeClass = PedidoTransfView::class
      )
      if (userSaci?.devCliente == true) route(
        icon = REPLY,
        title = "Dev Cliente",
        routeClass = DevClienteView::class,
      )
      if (userSaci?.pedidoRetira == true) route(
        icon = CART,
        title = "Retira",
        routeClass = PedidoRetiraView::class,
      )
      if (userSaci?.pedidoRetira == true) route(
        icon = CUBES,
        title = "Produto",
        routeClass = ProdutoView::class,
      )
      if (userSaci?.acertoEstoque == true) route(
        icon = PACKAGE,
        title = "Acerto Estoque",
        routeClass = AcertoEstoqueView::class,
      )
      if (userSaci?.estoqueCD == true) route(
        icon = STORAGE,
        title = "CD MF",
        routeClass = EstoqueCDView::class,
      )

      if (userSaci?.admin == true) route(icon = USER, title = "Usuário", routeClass = UsuarioView::class)
    }
  }
}
