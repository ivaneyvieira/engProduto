package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabPedidoTransfRessu4ViewModel(val viewModel: PedidoTransfViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaPedido.ENT)
    val pedidos = PedidoTransf.findTransf(filtro)
    subView.updatePedidos(pedidos)
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun marcaCD() {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaPedido.CD.num
      produtoNF.usuarioCD = ""
      produtoNF.salva()
      produtoNF.orcamento()
    }
    subView.updateProdutos()
  }

  val subView
    get() = viewModel.view.tabPedidoTransfRessu4
}

interface ITabPedidoTransfRessu4 : ITabView {
  fun filtro(marca: EMarcaPedido): FiltroPedidoTransf
  fun updatePedidos(pedidos: List<PedidoTransf>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoPedidoTransf>
}