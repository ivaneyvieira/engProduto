package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.PedidoAcerto
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoAcerto
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoRessuprimento
import kotlin.collections.flatMap

class TabAcertoPedidoViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoPedido

  fun updateView() = viewModel.exec {
    val pedido = PedidoAcerto.findPedidoAcerto()
    subView.updatePedido(pedido)
  }

  fun geraPlanilha(): ByteArray = viewModel.exec {
    val pedidos = subView.pedidoSelecionado().ifEmpty{
      fail("Nenhum pedido selecionado")
    }
    val produtos = pedidos.flatMap{
      it.produtos()
    }
    val planilha = PlanilhaProdutoAcerto()
    planilha.write(produtos)
  }
}

interface ITabAcertoPedido : ITabView {
  fun updatePedido(pedidos: List<PedidoAcerto>)
  fun pedidoSelecionado(): List<PedidoAcerto>
}
