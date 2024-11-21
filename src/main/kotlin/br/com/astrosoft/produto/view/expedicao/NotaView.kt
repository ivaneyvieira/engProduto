package br.com.astrosoft.produto.view.expedicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.expedicao.INotaView
import br.com.astrosoft.produto.viewmodel.expedicao.NotaViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "expedicao")
@PageTitle("Nota")
@CssImport("./styles/gridTotal.css")
@PermitAll
class NotaView : ViewLayout<NotaViewModel>(), INotaView, BeforeEnterObserver {
  override val viewModel = NotaViewModel(this)
  override val tabNotaSep = TabNotaSep(viewModel.tabNotaSepViewModel)
  override val tabNotaRota = TabNotaRota(viewModel.tabNotaRotaViewModel)
  override val tabNotaTroca = TabNotaTroca(viewModel.tabNotaTrocaViewModel)
  override val tabNotaExp = TabNotaExp(viewModel.tabNotaExpViewModel)
  override val tabNotaCD = TabNotaCD(viewModel.tabNotaCDViewModel)
  override val tabNotaEnt = TabNotaEnt(viewModel.tabNotaEntViewModel)
  override val tabNotaUsr = TabNotaUsr(viewModel.tabNotaUsrViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.nota
  }

  init {
    addTabSheat(viewModel)
  }
}