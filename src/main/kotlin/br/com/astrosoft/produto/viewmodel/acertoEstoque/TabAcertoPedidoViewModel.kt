package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.PedidoAcerto
import br.com.astrosoft.produto.model.beans.ProdutoAcerto
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoAcerto
import br.com.astrosoft.produto.model.printText.PrintPedidoAcerto

class TabAcertoPedidoViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoPedido

  fun updateView() = viewModel.exec {
    val user = AppConfig.userLogin() as? UserSaci
    val pedidosAcerto = PedidoAcerto.findPedidoAcerto()
    val pedido = pedidosAcerto.filter {
      (it.lojaPedido == user?.lojaAcerto) || (user?.lojaAcerto == 0) || (it.lojaPedido == 0)
    }
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

  fun previewPedido() {
    val acerto = subView.pedidoDialog() ?: fail("Nenhum pedido selecionado")
    val produtos = subView.produtosSelecionados().ifEmpty {
      fail("Nenhum produto selecionado")
    }

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

  fun removeProduto() = viewModel.exec {
    val produtos = subView.produtosSelecionados().ifEmpty {
      fail("Nenhum produto selecionado")
    }
    subView.confirmaLogin("Confirma a remoção do produto?", UserSaci::acertoRemoveProd) {
      produtos.forEach { produto ->
        produto.removerProduto()
      }
      subView.updateProdutos()
      updateView()
    }
  }
}

interface ITabAcertoPedido : ITabView {
  fun updatePedido(pedidos: List<PedidoAcerto>)
  fun pedidoSelecionado(): List<PedidoAcerto>
  fun produtosSelecionados(): List<ProdutoAcerto>
  fun confirmaLogin(msg: String, permissao: UserSaci.() -> Boolean, onLogin: () -> Unit)
  fun updateProdutos()
  fun pedidoDialog(): PedidoAcerto?
}
