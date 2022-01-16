package br.com.astrosoft.produto.viewmodel.pedido

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.DadosEtiquetaPedido
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabPedidoEntViewModel(val viewModel: PedidoViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaPedido.ENT)
    val Pedidos = PedidoVenda.find(filtro)
    subView.updatePedidos(Pedidos)
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
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreview(impressora,
                                   DadosEtiquetaPedido(titulo = "Entregue",
                                                 usuario = pedido.usuarioNameCD,
                                                 pedido = pedido.ordno.toString(),
                                                 data = pedido.dataCD,
                                                 hora = pedido.horaCD,
                                                 local = pedido.localizacao ?: ""))
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
  fun filtro(marca: EMarcaPedido): FiltroPedido
  fun updatePedidos(pedidos: List<PedidoVenda>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoPedidoVenda>
}