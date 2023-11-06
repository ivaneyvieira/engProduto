package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabPedidoTransfEntViewModel(val viewModel: PedidoTransfViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaPedido.ENT)
    val pedidos = PedidoVenda.find(filtro)
    subView.updatePedidos(pedidos)
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

  fun printEtiqueta(pedido: PedidoVenda?) = viewModel.exec {
    pedido ?: fail("Nenhum pedido selecionado")
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, pedido.produtos(EMarcaPedido.ENT))
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  val subView
    get() = viewModel.view.tabPedidoTransfEnt
}

interface ITabPedidoTransfEnt : ITabView {
  fun filtro(marca: EMarcaPedido): FiltroPedido
  fun updatePedidos(pedidos: List<PedidoVenda>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoPedidoVenda>
}