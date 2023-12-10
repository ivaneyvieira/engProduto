package br.com.astrosoft.produto.view.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.retira.IPedidoRetiraView
import br.com.astrosoft.produto.viewmodel.retira.PedidoRetiraViewModel
import com.github.mvysny.karibudsl.v23.tabSheet
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = ProdutoLayout::class, value = "retira")
@PageTitle("Retira")
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

  override fun showRelatorioPedidoMinuta(pedidos: List<Pedido>) {
    //TODO
  }

  override fun showRelatorioPedido(pedidos: List<Pedido>) {
    //TODO
  }

  private fun showRelatorio(byteArray: ByteArray) {
    //TODO
  }
}

