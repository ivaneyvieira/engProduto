package br.com.astrosoft.produto.view.pedido

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.view.Reroute
import br.com.astrosoft.produto.viewmodel.pedido.IPedidoView
import br.com.astrosoft.produto.viewmodel.pedido.PedidoViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = ProdutoLayout::class, value = "pedido")
@PageTitle("Pedido")
@CssImport("./styles/gridTotal.css")
class PedidoView : ViewLayout<PedidoViewModel>(), IPedidoView {
  override val viewModel = PedidoViewModel(this)
  override val tabPedidoCD = TabPedidoCD(viewModel.tabPedidoCDViewModel)
  override val tabPedidoEnt = TabPedidoEnt(viewModel.tabPedidoEntViewModel)

  override fun isAccept(user: IUser): Boolean {
    val userSaci = user as? UserSaci ?: return false
    return userSaci.nota
  }

  init {
    addTabSheat(viewModel)
  }

  override fun beforeEnter(event: BeforeEnterEvent?) {
    val userSaci = Config.user as? UserSaci
    if (userSaci?.pedido == false) event?.rerouteTo(Reroute::class.java)
  }
}