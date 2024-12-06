package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroPedidoRessuprimento
import br.com.astrosoft.produto.model.beans.PedidoNovo
import br.com.astrosoft.produto.model.beans.PedidoRessuprimento
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento

class TabPedidoRessuprimentoViewModel(val viewModel: RessuprimentoViewModel) {
  val subView
    get() = viewModel.view.tabPedidoRessuprimento

  fun updateView() {
    val filtro = subView.filtro()
    val pedidos = PedidoRessuprimento.findPedidoRessuprimento(filtro)
    subView.updatePedidos(pedidos)
  }

  fun imprimePedido() = viewModel.exec {
  }

  fun separaPedido() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) fail("Nenhum pedido selecionado")


    viewModel.view.showQuestion("Confirma a separação do pedido?") {
      var ordnoNovo: Int = produtos.firstOrNull()?.pedidoNovo()?.ordno ?: 0
      produtos.forEach { produto ->
        produto.separaPedido(ordnoNovo)
      }

      if (ordnoNovo == 0) {
        viewModel.view.showInformation("Não foi gerado o pedido")
      } else {
        viewModel.view.showInformation("Foi gerado o pedido número $ordnoNovo")
      }
      subView.updateProdutos()
      updateView()
    }
  }

  fun duplicaPedido() = viewModel.exec {
    val pedidos = subView.predidoSelecionado()
    if (pedidos.isEmpty()) fail("Nenhum pedido selecionado")
    if (pedidos.size > 1) fail("Selecione apenas um pedido para duplicar")
    val pedido = pedidos.first()

    viewModel.view.showQuestion("Confirma a duplicação do pedido?") {
      val pedidoNovo = pedido.duplicaPedido()

      if (pedidoNovo == null) {
        viewModel.view.showInformation("Não foi gerado o pedido")
      } else {
        viewModel.view.showInformation("Foi gerado o pedido número ${pedidoNovo.ordno}")
      }

      updateView()
    }
  }
}

interface ITabPedidoRessuprimento : ITabView {
  fun filtro(): FiltroPedidoRessuprimento
  fun updatePedidos(pedido: List<PedidoRessuprimento>)
  fun predidoSelecionado(): List<PedidoRessuprimento>
  fun produtosSelecionados(): List<ProdutoRessuprimento>
  fun updateProdutos()
}