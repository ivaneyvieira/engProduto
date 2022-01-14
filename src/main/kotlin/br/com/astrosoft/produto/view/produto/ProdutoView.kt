package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.view.Reroute
import br.com.astrosoft.produto.viewmodel.produto.IProdutoView
import br.com.astrosoft.produto.viewmodel.produto.ProdutoViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = ProdutoLayout::class, value = "produto")
@PageTitle("Produto")
@CssImport("./styles/gridTotal.css")
class ProdutoView : ViewLayout<ProdutoViewModel>(), IProdutoView {
  override val viewModel: ProdutoViewModel = ProdutoViewModel(this)
  override val tabProdutoList = TabProdutoList(viewModel.tabProdutoListViewModel)
  override val tabProdutoReserva = TabProdutoReserva(viewModel.tabProdutoReservaViewModel)
  override val tabProdutoRetiraEntrega = TabProdutoRetiraEntrega(viewModel.tabProdutoRetiraEntregaViewModel)
  override val tabProdutoRetiraEntregaEdit = TabProdutoRetiraEntregaEdit(viewModel.tabProdutoRetiraEntregaEditViewModel)

  override fun isAccept(user: IUser): Boolean {
    val userSaci = user as? UserSaci ?: return false
    return userSaci.produtoList
  }

  init {
    addTabSheat(viewModel)
  }

  override fun beforeEnter(event: BeforeEnterEvent?) {
    val userSaci = Config.user as? UserSaci
    if (userSaci?.produto == false) event?.rerouteTo(Reroute::class.java)
  }
}

