package br.com.astrosoft.produto.viewmodel.pedido

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.DadosEtiqueta
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
    }
    subView.updateProdutos()
    updateView()
  }

  fun printEtiqueta(pedido: PedidoVenda?) = viewModel.exec {
    val chave = pedido?.chaveCD ?: fail("Chave não encontrada")
    val split = chave.split("_")
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreview(impressora,
                                   DadosEtiqueta(titulo = "Entregue",
                                                 usuario = split.getOrNull(1) ?: "",
                                                 nota = split.getOrNull(2) ?: "",
                                                 data = split.getOrNull(3) ?: "",
                                                 hora = split.getOrNull(4) ?: "",
                                                 local = split.getOrNull(5)
                                                         ?: ""))
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