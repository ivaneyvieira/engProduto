package br.com.astrosoft.produto.view.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.retira.IPedidoRetiraView
import br.com.astrosoft.produto.viewmodel.retira.PedidoRetiraViewModel
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "retira")
@PageTitle("Retira")
@PermitAll
class PedidoRetiraView : ViewLayout<PedidoRetiraViewModel>(), IPedidoRetiraView {
  override val viewModel: PedidoRetiraViewModel = PedidoRetiraViewModel(this)
  override val tabRetiraImprimir = TabRetiraImprimir(viewModel.tabRetiraImprimirViewModel)
  override val tabRetiraImpressoSemNota = TabRetiraImpressoSemNota(viewModel.tabRetiraImpressoSemNotaViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.pedidoRetira
  }

  init {
    addTabSheat(viewModel)
  }
}

