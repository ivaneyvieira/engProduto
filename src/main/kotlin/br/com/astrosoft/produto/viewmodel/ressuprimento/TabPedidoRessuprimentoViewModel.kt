package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroPedidoRessuprimento
import br.com.astrosoft.produto.model.beans.PedidoRessuprimento

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

  fun duplicaPedido() = viewModel.exec {
    val pedidos = subView.predidoSelecionado()
    if (pedidos.isEmpty()) fail("Nenhum pedido selecionado")
    if (pedidos.size > 1) fail("Selecione apenas um pedido para duplicar")
    val pedido = pedidos.first()

    viewModel.view.showQuestion("Confirma a duplicação do pedido?") {
      pedido.duplicaPedido()
      updateView()
    }
  }
}

interface ITabPedidoRessuprimento : ITabView {
  fun filtro(): FiltroPedidoRessuprimento
  fun updatePedidos(pedido: List<PedidoRessuprimento>)
  fun predidoSelecionado(): List<PedidoRessuprimento>
}