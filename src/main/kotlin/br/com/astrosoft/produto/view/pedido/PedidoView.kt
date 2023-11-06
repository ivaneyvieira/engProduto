package br.com.astrosoft.produto.view.pedido

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.pedido.IPedidoView
import br.com.astrosoft.produto.viewmodel.pedido.PedidoViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "pedido")
@PageTitle("Pedido")
@CssImport("./styles/gridTotal.css")
@PermitAll
class PedidoView : ViewLayout<PedidoViewModel>(), IPedidoView {
  override val viewModel = PedidoViewModel(this)
  override val tabPedidoCD = TabPedidoCD(viewModel.tabPedidoCDViewModel)
  override val tabPedidoEnt = TabPedidoEnt(viewModel.tabPedidoEntViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.nota
  }

  init {
    addTabSheat(viewModel)
  }
}