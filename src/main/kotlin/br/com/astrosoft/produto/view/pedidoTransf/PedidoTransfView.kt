package br.com.astrosoft.produto.view.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.pedidoTransf.IPedidoTransfView
import br.com.astrosoft.produto.viewmodel.pedidoTransf.PedidoTransfViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "pedido")
@PageTitle("Pedido")
@CssImport("./styles/gridTotal.css")
@PermitAll
class PedidoTransfView : ViewLayout<PedidoTransfViewModel>(), IPedidoTransfView {
  override val viewModel = PedidoTransfViewModel(this)
  override val tabPedidoTransfCD = TabPedidoTransfCD(viewModel.tabPedidoTransfCDViewModel)
  override val tabPedidoTransfEnt = TabPedidoTransfEnt(viewModel.tabPedidoTransfEntViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.pedido
  }

  init {
    addTabSheat(viewModel)
  }
}