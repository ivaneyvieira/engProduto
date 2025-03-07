package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.devCliente.DevClienteViewModel
import br.com.astrosoft.produto.viewmodel.devCliente.IDevClienteView
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "devCliente")
@PageTitle("Dev Cliente")
@CssImport("./styles/gridTotal.css")
@PermitAll
class DevClienteView : ViewLayout<DevClienteViewModel>(), IDevClienteView {
  override val viewModel = DevClienteViewModel(this)
  override val tabDevCliEditor = TabDevCliEditor(viewModel.tabDevCliEditorViewModel)
  override val tabDevCliImprimir = TabDevCliImprimir(viewModel.tabDevCliImprimirViewModel)
  override val tabDevCliDevTroca = TabDevCliDevTroca(viewModel.tabDevCliDevTrocaViewModel)
  override val tabDevCliValeTrocaProduto = TabDevCliValeTrocaProduto(viewModel.tabDevCliValeTrocaProdutoViewModel)
  override val tabDevCliImpresso = TabDevCliImpresso(viewModel.tabDevCliImpressoViewModel)
  override val tabDevCliCredito = TabDevCliCredito(viewModel.tabDevCliCreditoViewModel)
  override val tabDevCliVenda = TabDevVenda(viewModel.tabDevCliVendaViewModel)
  override val tabDevCliUsr = TabDevCliUsr(viewModel.tabDevCliUsrViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.devCliente
  }

  init {
    addTabSheat(viewModel)
  }
}