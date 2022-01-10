package br.com.astrosoft.produto.view.nota

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.nota.INotaView
import br.com.astrosoft.produto.viewmodel.nota.NotaViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = ProdutoLayout::class, value = "nota")
@PageTitle("Nota")
@CssImport("./styles/gridTotal.css")
class NotaView : ViewLayout<NotaViewModel>(), INotaView {
  override val viewModel = NotaViewModel(this)
  override val tabNotaExp = TabNotaExp(viewModel.tabNotaExpViewModel)
  override val tabNotaCD = TabNotaCD(viewModel.tabNotaCDViewModel)
  override val tabNotaEnt = TabNotaEnt(viewModel.tabNotaEntViewModel)

  override fun isAccept(user: IUser): Boolean {
    val userSaci = user as? UserSaci ?: return false
    return userSaci.nota
  }

  init {
    addTabSheat(viewModel)
  }
}