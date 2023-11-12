package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*

class TabPedidoTransfRessu4ViewModel(val viewModel: PedidoTransfViewModel) {
  fun updateView() {
    val filtro = subView.filtro()
    val pedidos = TransfRessu4.findAll(filtro)
    subView.updatePedidos(pedidos)
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun geraPlanilha(pedido: TransfRessu4) {
    TODO("Not yet implemented")
  }

  fun imprimeRelatorio(pedido: TransfRessu4) {
    TODO("Not yet implemented")
  }

  val subView
    get() = viewModel.view.tabPedidoTransfRessu4
}

interface ITabPedidoTransfRessu4 : ITabView {
  fun filtro(): FiltroPedidoRessu4
  fun updatePedidos(pedidos: List<TransfRessu4>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoTransfRessu4>
}