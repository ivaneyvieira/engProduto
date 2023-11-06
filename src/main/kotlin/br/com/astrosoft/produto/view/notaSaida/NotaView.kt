package br.com.astrosoft.produto.view.notaSaida

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.notaSaida.INotaView
import br.com.astrosoft.produto.viewmodel.notaSaida.NotaViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "notaSaida")
@PageTitle("Nota")
@CssImport("./styles/gridTotal.css")
@PermitAll
class NotaView : ViewLayout<NotaViewModel>(), INotaView, BeforeEnterObserver {
  override val viewModel = NotaViewModel(this)
  override val tabNotaExp = TabNotaExp(viewModel.tabNotaExpViewModel)
  override val tabNotaCD = TabNotaCD(viewModel.tabNotaCDViewModel)
  override val tabNotaEnt = TabNotaEnt(viewModel.tabNotaEntViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.nota
  }

  init {
    addTabSheat(viewModel)
  }
}