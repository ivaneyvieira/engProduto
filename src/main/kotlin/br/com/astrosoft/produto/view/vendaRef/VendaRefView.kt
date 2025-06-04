package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.vendaRef.IVendaRefView
import br.com.astrosoft.produto.viewmodel.vendaRef.VendaRefViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "venda")
@PageTitle("Venda")
@CssImport("./styles/gridTotal.css")
@PermitAll
class VendaRefView() : ViewLayout<VendaRefViewModel>(), IVendaRefView {
  override val viewModel = VendaRefViewModel(this)
  override val tabVendaRef = TabVendaRef(viewModel.tabVendaRefViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.vendaRef
  }

  init {
    addTabSheat(viewModel)
  }
}