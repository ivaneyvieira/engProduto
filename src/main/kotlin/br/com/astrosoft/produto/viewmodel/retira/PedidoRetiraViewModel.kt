package br.com.astrosoft.produto.viewmodel.retira

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.pedido.model.beans.Pedido

class PedidoRetiraViewModel(view: IPedidoRetiraView) : ViewModel<IPedidoRetiraView>(view) {
  val tabRetiraImprimirViewModel = PedidoRetiraImprimirViewModel(this)
  val tabRetiraImpressoSemNotaViewModel = PedidoRetiraImpressoSemNotaViewModel(this)
}

interface IPedidoRetiraView : IView {
  val tabRetiraImprimir: IPedidoRetiraImprimir
  val tabRetiraImpressoSemNota: IPedidoRetiraImpressoSemNota

  //
  fun showRelatorioPedidoMinuta(pedidos: List<Pedido>)

  fun showRelatorioPedido(pedidos: List<Pedido>)
}