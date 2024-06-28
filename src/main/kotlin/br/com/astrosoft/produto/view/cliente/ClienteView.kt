package br.com.astrosoft.produto.view.cliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.cliente.ClienteViewModel
import br.com.astrosoft.produto.viewmodel.cliente.IClienteView
import br.com.astrosoft.produto.viewmodel.cliente.ITabCliente
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "cliente")
@PageTitle("Cliente")
@CssImport("./styles/gridTotal.css")
@PermitAll
class ClienteView() : ViewLayout<ClienteViewModel>(), IClienteView {
  override val viewModel = ClienteViewModel(this)

  override val tabCliente = TabCliente(viewModel.tabClienteViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.cliente
  }

  init {
    addTabSheat(viewModel)
  }
}