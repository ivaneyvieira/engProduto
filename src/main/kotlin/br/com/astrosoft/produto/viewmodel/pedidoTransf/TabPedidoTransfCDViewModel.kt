package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.EtiquetaChave
import java.time.LocalDate
import java.time.LocalTime

class TabPedidoTransfCDViewModel(val viewModel: PedidoTransfViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaPedido.CD)
    val pedidos = PedidoVenda.findTransf(filtro)
    subView.updatePedidos(pedidos)
  }

  fun marcaEnt() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produto ->
      produto.marca = EMarcaPedido.ENT.num
      val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
      val usuario = AppConfig.userLogin()?.login ?: ""
      produto.usuarioCD = "$usuario-$dataHora"
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun marcaEntProdutos(codigoBarra: String) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.marca = EMarcaPedido.ENT.num
    val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
    val usuario = AppConfig.userLogin()?.login ?: ""
    produto.usuarioCD = "$usuario-$dataHora"
    subView.updateProduto(produto)
  }

  fun salvaProdutos() = viewModel.exec {
    val itens = subView.produtosMarcados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
    val usuario = AppConfig.userLogin()?.login ?: ""
    itens.forEach { produto ->
      produto.usuarioCD = "$usuario-$dataHora"
      produto.salva()
      produto.expira()
    }
    imprimeEtiquetaEnt(itens)
    subView.updateProdutos()
  }

  private fun imprimeEtiquetaEnt(produto: List<ProdutoPedidoVenda>) {
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, produto)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  val subView
    get() = viewModel.view.tabPedidoTransfCD
}

interface ITabPedidoTransfCD : ITabView {
  fun filtro(marca: EMarcaPedido): FiltroPedido
  fun updatePedidos(pedidos: List<PedidoVenda>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoPedidoVenda>
  fun produtosMarcados(): List<ProdutoPedidoVenda>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoPedidoVenda?
  fun findPedido(): PedidoVenda?
  fun updateProduto(produto: ProdutoPedidoVenda)
}