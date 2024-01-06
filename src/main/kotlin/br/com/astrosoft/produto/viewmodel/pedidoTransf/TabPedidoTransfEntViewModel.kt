package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroPedidoTransf
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.PedidoTransf
import br.com.astrosoft.produto.model.printText.TransferenciaEntregue

class TabPedidoTransfEntViewModel(val viewModel: PedidoTransfViewModel) {
  fun updateView() {
    val filtro = subView.filtro()
    val pedidos = PedidoTransf.findTransf(filtro).filter {
      it.situacao != 5
    }
    subView.updatePedidos(pedidos)
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun previewPedido(pedido: PedidoTransf) {
    val relatorio = TransferenciaEntregue(pedido)
    val rota = pedido.rotaPedido()
    relatorio.print(
      dados = pedido.produtos(),
      printer = subView.printerPreview(rota = rota, loja = pedido.lojaNoDes ?: 0)
    )
  }

  val subView
    get() = viewModel.view.tabPedidoTransfEnt
}

interface ITabPedidoTransfEnt : ITabView {
  fun filtro(): FiltroPedidoTransf
  fun updatePedidos(pedidos: List<PedidoTransf>)
}