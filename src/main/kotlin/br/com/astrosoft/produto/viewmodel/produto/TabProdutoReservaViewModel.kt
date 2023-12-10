package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.DadosPedido
import br.com.astrosoft.produto.model.beans.FiltroProduto
import br.com.astrosoft.produto.model.beans.ProdutoReserva

class TabProdutoReservaViewModel(val viewModel: ProdutoViewModel) {
  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoReserva.find(filtro)
    subView.updateProdutos(produtos)
  }

  fun expiraPedidosSelecionados() = viewModel.exec {
    val pedidos: List<DadosPedido> = subView.pedidosSelecionado().ifEmpty { fail("Nenhum pedido selecionado") }
    pedidos.forEach { pedido ->
      pedido.expira()
    }
    subView.updateComponent()
  }

  val subView
    get() = viewModel.view.tabProdutoReserva
}

interface ITabProdutoReserva : ITabView {
  fun filtro(): FiltroProduto
  fun updateProdutos(produtos: List<ProdutoReserva>)
  fun pedidosSelecionado(): List<DadosPedido>
}