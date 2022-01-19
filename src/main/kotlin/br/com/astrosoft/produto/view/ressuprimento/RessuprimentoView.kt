package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.view.Reroute
import br.com.astrosoft.produto.viewmodel.ressuprimento.IRessuprimentoView
import br.com.astrosoft.produto.viewmodel.ressuprimento.RessuprimentoViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = ProdutoLayout::class, value = "ressuprimento")
@PageTitle("Ressuprimento")
@CssImport("./styles/gridTotal.css")
class RessuprimentoView : ViewLayout<RessuprimentoViewModel>(), IRessuprimentoView {
  override val viewModel = RessuprimentoViewModel(this)
  override val tabRessuprimentoCD = TabRessuprimentoCD(viewModel.tabRessuprimentoCDViewModel)
  override val tabRessuprimentoEnt = TabRessuprimentoEnt(viewModel.tabRessuprimentoEntViewModel)

  override fun isAccept(user: IUser): Boolean {
    val userSaci = user as? UserSaci ?: return false
    return userSaci.nota
  }

  init {
    addTabSheat(viewModel)
  }

  override fun beforeEnter(event: BeforeEnterEvent?) {
    val userSaci = Config.user as? UserSaci
    if (userSaci?.ressuprimento == false) event?.rerouteTo(Reroute::class.java)
  }
}