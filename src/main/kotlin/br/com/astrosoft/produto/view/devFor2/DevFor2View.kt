package br.com.astrosoft.produto.view.devFor2

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.devFor2.DevFor2ViewModel
import br.com.astrosoft.produto.viewmodel.devFor2.IDevFor2View
import br.com.astrosoft.produto.viewmodel.devFor2.ITabNotaUsr
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "devfor2")
@PageTitle("DevFor 2")
@CssImport("./styles/gridTotal.css")
@PermitAll
class DevFor2View : ViewLayout<DevFor2ViewModel>(), IDevFor2View {
  override val viewModel = DevFor2ViewModel(this)
  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.devFor2
  }

  override val tabNotaPendencia = TabNotaPendencia(viewModel.tabNotaPendenciaViewModel)
  override val tabNotaNFD = TabNotaNFD(viewModel.tabNotaNFDViewModel)
  override val tabNotaTransportadora = TabNotaTransportadora(viewModel.tabNotaTransportadoraViewModel)
  override val tabNotaEmail = TabNotaEmail(viewModel.tabNotaEmailViewModel)
  override val tabNotaUsr = TabNotaUsr(viewModel.tabNotaUsrViewModel)

  init {
    addTabSheat(viewModel)
  }
}