package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.layout.AppLayoutAbstract
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.acertoEstoque.AcertoEstoqueView
import br.com.astrosoft.produto.view.cliente.ClienteView
import br.com.astrosoft.produto.view.devCliente.DevClienteView
import br.com.astrosoft.produto.view.devForReceb.DevFor2View
import br.com.astrosoft.produto.view.estoqueCD.EstoqueCDView
import br.com.astrosoft.produto.view.expedicao.NotaView
import br.com.astrosoft.produto.view.nfd.NfdView
import br.com.astrosoft.produto.view.pedidoTransf.PedidoTransfView
import br.com.astrosoft.produto.view.precificacao.PrecificacaoView
import br.com.astrosoft.produto.view.produto.ProdutoView
import br.com.astrosoft.produto.view.recebimento.RecebimentoView
import br.com.astrosoft.produto.view.reposicao.ReposicaoView
import br.com.astrosoft.produto.view.ressuprimento.RessuprimentoView
import br.com.astrosoft.produto.view.retira.PedidoRetiraView
import br.com.astrosoft.produto.view.vendaRef.VendaRefView
import com.github.mvysny.karibudsl.v23.route
import com.github.mvysny.karibudsl.v23.sideNav
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.icon.VaadinIcon.*

class ProdutoLayout : AppLayoutAbstract() {
  override fun HasComponents.navigation() {
    sideNav {
      val userSaci = AppConfig.userLogin() as? UserSaci
      if (userSaci?.recebimento == true) route(
        icon = TRUCK,
        label = "Recebimento",
        routeClass = RecebimentoView::class
      )
      if (userSaci?.ressuprimento == true) route(
        icon = SHOP,
        label = "Ressuprimento",
        routeClass = RessuprimentoView::class
      )
      if (userSaci?.nota == true) route(
        icon = OUT,
        label = "Expedição",
        routeClass = NotaView::class
      )
      if (userSaci?.reposicao == true) route(
        icon = SIGNAL,
        label = "Reposição Loja",
        routeClass = ReposicaoView::class
      )
      if (userSaci?.pedidoTransf == true) route(
        icon = EXCHANGE,
        label = "Pedido Transf",
        routeClass = PedidoTransfView::class
      )
      if (userSaci?.devCliente == true) route(
        icon = REPLY,
        label = "Dev Cliente",
        routeClass = DevClienteView::class,
      )
      if (userSaci?.cliente == true) route(
        icon = SMILEY_O,
        label = "Cliente",
        routeClass = ClienteView::class,
      )
      if (userSaci?.vendaRef == true) route(
        icon = SHOP,
        label = "Venda",
        routeClass = VendaRefView::class,
      )
      if (userSaci?.pedidoRetira == true) route(
        icon = CART,
        label = "Retira",
        routeClass = PedidoRetiraView::class,
      )
      if (userSaci?.produtoList == true) route(
        icon = CUBES,
        label = "Produto",
        routeClass = ProdutoView::class,
      )
      if (userSaci?.precificacao == true) route(
        icon = MONEY,
        label = "Precificação",
        routeClass = PrecificacaoView::class,
      )
      if (userSaci?.acertoEstoque == true) route(
        icon = PACKAGE,
        label = "Acerto Estoque",
        routeClass = AcertoEstoqueView::class,
      )
      if (userSaci?.nfd == true) route(
        icon = INBOX,
        label = "NFD",
        routeClass = NfdView::class,
      )
      if (userSaci?.devFor2 == true) {
        route(
          icon = FORM,
          label = "Dev Fornecedor",
          routeClass = DevFor2View::class
        )
      }
      if (userSaci?.estoqueCD == true) route(
        icon = STORAGE,
        label = "Controle Estoque",
        routeClass = EstoqueCDView::class,
      )

      if (userSaci?.admin == true) route(icon = USER, label = "Usuário", routeClass = UsuarioView::class)
    }
  }
}
