package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.recebimento.IRecebimentoView
import br.com.astrosoft.produto.viewmodel.recebimento.RecebimentoViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "recebimento")
@PageTitle("Recebimento")
@CssImport("./styles/gridTotal.css")
@PermitAll
class RecebimentoView : ViewLayout<RecebimentoViewModel>(), IRecebimentoView {
  override val viewModel = RecebimentoViewModel(this)
  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.recebimento
  }

  override val tabPedido = TabPedido(viewModel.tabPedidoViewModel)
  override val tabAgenda = TabAgenda(viewModel.tabAgendaViewModel)
  override val tabFileNFE = TabRecebimentoXML(viewModel.tabFileNFEViewModel)
  override val tabReceberNota = TabReceberNota(viewModel.tabReceberNotaViewModel)
  override val tabNotaRecebida = TabNotaRecebida(viewModel.tabNotaRecebidaViewModel)
  override val tabRecebimentoUsr = TabRecebimentoUsr(viewModel.tabRecebimentoUsrViewModel)

  init {
    addTabSheat(viewModel)
  }
}