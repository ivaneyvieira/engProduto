package br.com.astrosoft.produto.viewmodel.retira

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class PedidoRetiraViewModel(view: IPedidoRetiraView) : ViewModel<IPedidoRetiraView>(view) {
  val tabRetiraImprimirViewModel = PedidoRetiraImprimirViewModel(this)
  val tabRetiraImpressoViewModel = PedidoRetiraImpressoViewModel(this)
  val tabRetiraUsrViewModel = PedidoRetiraUsrViewModel(this)
  override fun listTab(): List<ITabView> = listOf(view.tabRetiraImprimir, view.tabRetiraImpresso, view.tabRetiraUsr)
}

interface IPedidoRetiraView : IView {
  val tabRetiraImprimir: IPedidoRetiraImprimir
  val tabRetiraImpresso: IPedidoRetiraImpresso
  val tabRetiraUsr: ITabPedidoRetiraUsr
}