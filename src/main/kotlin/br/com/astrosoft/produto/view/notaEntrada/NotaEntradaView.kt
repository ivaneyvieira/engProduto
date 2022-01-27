package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.view.Reroute
import br.com.astrosoft.produto.viewmodel.notaEntrada.INotaEntradaView
import br.com.astrosoft.produto.viewmodel.notaEntrada.NotaEntradaViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = ProdutoLayout::class, value = "notaEntrada")
@PageTitle("Nota Entrada")
@CssImport("./styles/gridTotal.css")
class NotaEntradaView: ViewLayout<NotaEntradaViewModel>(), INotaEntradaView, BeforeEnterObserver {
  override val viewModel = NotaEntradaViewModel(this)
  override val tabNotaEntradaReceber = TabNotaEntradaReceber(viewModel.tabNotaReceberViewModel)
  override val tabNotaEntradaPendente = TabNotaEntradaPendente(viewModel.tabNotaPendenteViewModel)

  override fun isAccept(user: IUser): Boolean {
    val userSaci = user as? UserSaci ?: return false
    return userSaci.notaEntrada
  }

  init {
    addTabSheat(viewModel)
  }

  override fun beforeEnter(event: BeforeEnterEvent?) {
    val userSaci = Config.user as? UserSaci
    if (userSaci?.notaEntrada == false) event?.rerouteTo(Reroute::class.java)
  }
}