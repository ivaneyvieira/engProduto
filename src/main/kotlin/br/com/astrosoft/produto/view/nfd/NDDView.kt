package br.com.astrosoft.produto.view.nfd

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.nfd.INfdView
import br.com.astrosoft.produto.viewmodel.nfd.NfdViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "NFD")
@PageTitle("Nfd")
@CssImport("./styles/gridTotal.css")
@PermitAll
class NfdView : ViewLayout<NfdViewModel>(), INfdView, BeforeEnterObserver {
  override val viewModel = NfdViewModel(this)
  override val tabNfdDevFor = TabNfdDevFor(viewModel.tabNfdDevForViewModel)
  override val tabNfdUsr = TabNfdUsr(viewModel.tabNfdUsrViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.nfd
  }

  init {
    addTabSheat(viewModel)
  }
}