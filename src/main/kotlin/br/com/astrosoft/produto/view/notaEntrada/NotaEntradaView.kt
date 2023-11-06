package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.notaEntrada.INotaEntradaView
import br.com.astrosoft.produto.viewmodel.notaEntrada.NotaEntradaViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "notaEntrada")
@PageTitle("Nota Entrada")
@CssImport("./styles/gridTotal.css")
@PermitAll
class NotaEntradaView : ViewLayout<NotaEntradaViewModel>(), INotaEntradaView, BeforeEnterObserver {
  override val viewModel = NotaEntradaViewModel(this)
  override val tabNotaEntradaReceber = TabNotaEntradaReceber(viewModel.tabNotaReceberViewModel)
  override val tabNotaEntradaRecebido = TabNotaEntradaRecebido(viewModel.tabNotaRecebidoViewModel)
  override val tabNotaEntradaBase = TabNotaEntradaBase(viewModel.tabNotaBaseViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.notaEntrada
  }

  init {
    addTabSheat(viewModel)
  }
}