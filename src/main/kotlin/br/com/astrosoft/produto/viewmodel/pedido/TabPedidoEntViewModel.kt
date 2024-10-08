package br.com.astrosoft.produto.viewmodel.pedido

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabPedidoEntViewModel(val viewModel: PedidoViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaPedido.ENT)
    val pedidos = PedidoVenda.findVenda(filtro)
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
        EtiquetaChave.printPreviewEnt(setOf(impressora), pedido.produtos(EMarcaPedido.ENT), 1)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  val subView
    get() = viewModel.view.tabPedidoEnt
}

interface ITabPedidoEnt : ITabView {
  fun filtro(marca: EMarcaPedido): FiltroPedidoVenda
  fun updatePedidos(pedidos: List<PedidoVenda>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoPedidoVenda>
}