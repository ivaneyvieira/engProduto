package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.devCliente.DevClienteViewModel
import br.com.astrosoft.produto.viewmodel.devCliente.IDevClienteView
import br.com.astrosoft.produto.viewmodel.pedido.IPedidoView
import br.com.astrosoft.produto.viewmodel.pedido.PedidoViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "devCliente")
@PageTitle("Dev Cliente")
@CssImport("./styles/gridTotal.css")
@PermitAll
class DevClienteView : ViewLayout<DevClienteViewModel>(), IDevClienteView {
  override val viewModel = DevClienteViewModel(this)
  override val tabDevCliValeTroca = TabDevCliValeTroca(viewModel.tabDevCliValeTrocaViewModel)
  override val tabDevCliValeTrocaProduto = TabDevCliValeTrocaProduto(viewModel.tabDevCliValeTrocaProdutoViewModel)
  override val tabDevCliValeTrocaImp = TabDevCliValeTrocaImp(viewModel.tabDevCliValeTrocaImpViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.devCliente
  }

  init {
    addTabSheat(viewModel)
  }
}