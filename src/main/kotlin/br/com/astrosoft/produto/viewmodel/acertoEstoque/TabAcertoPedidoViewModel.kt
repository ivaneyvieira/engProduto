package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.PedidoAcerto
import br.com.astrosoft.produto.model.beans.ProdutoAcerto
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoAcerto
import br.com.astrosoft.produto.model.printText.PrintPedidoAcerto

class TabAcertoPedidoViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoPedido

  fun updateView() = viewModel.exec {
    val pedido = PedidoAcerto.findPedidoAcerto()
    subView.updatePedido(pedido)
  }

  fun geraPlanilha(): ByteArray = viewModel.exec {
    val pedidos = subView.pedidoSelecionado().ifEmpty {
      fail("Nenhum pedido selecionado")
    }
    val produtos = pedidos.flatMap {
      it.produtos()
    }
    val planilha = PlanilhaProdutoAcerto()
    planilha.write(produtos)
  }

  fun previewPedido(acerto: PedidoAcerto) {
    val produtos = acerto.produtos()

    val relatorio = PrintPedidoAcerto(acerto, ProdutoAcerto::qtPedido)

    relatorio.print(
      dados = produtos.sortedWith(
        compareBy(
          ProdutoAcerto::descricao,
          ProdutoAcerto::codigo,
          ProdutoAcerto::grade
        )
      ),
      printer = subView.printerPreview(loja = 1)
    )
  }
}

interface ITabAcertoPedido : ITabView {
  fun updatePedido(pedidos: List<PedidoAcerto>)
  fun pedidoSelecionado(): List<PedidoAcerto>
}
