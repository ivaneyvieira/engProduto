package br.com.astrosoft.produto.view.fornecedor

import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.produto.ProdutoViewModel
import br.com.astrosoft.produto.viewmodel.produto.IProdutoView
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.ViewLayout
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = ProdutoLayout::class)
@PageTitle("Produto")
@CssImport("./styles/gridTotal.css")
class ProdutoView : ViewLayout<ProdutoViewModel>(), IProdutoView {
  override val viewModel: ProdutoViewModel = ProdutoViewModel(this)
  override val tabProdutoList = TabProdutoList(viewModel.tabProdutoListViewModel)

  override fun isAccept(user: IUser): Boolean {
    val userSaci = user as? UserSaci ?: return false
    return userSaci.fornecedorList
  }

  init {
    addTabSheat(viewModel)
  }
}

