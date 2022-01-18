package br.com.astrosoft.produto.viewmodel.pedido

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.DadosEtiquetaPedido
import br.com.astrosoft.produto.model.zpl.EtiquetaChave
import java.time.LocalDate
import java.time.LocalTime

class TabPedidoCDViewModel(val viewModel: PedidoViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaPedido.CD)
    val Pedidos = PedidoVenda.find(filtro)
    subView.updatePedidos(Pedidos)
  }

  fun findGrade(prd: ProdutoPedidoVenda?, block: (List<PrdGrade>) -> Unit) = viewModel.exec {
    prd ?: return@exec
    val list = prd.findGrades()
    block(list)
  }

  fun marcaEnt() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produto ->
      produto.marca = EMarcaPedido.ENT.num
      val dataHora = LocalDate.now().format() + "_" + LocalTime.now().format()
      val usuario = Config.user?.login ?: ""
      produto.usuarioCD = usuario + "_" + dataHora
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun marcaEntProdutos(codigoBarra: String) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.marca = EMarcaPedido.ENT.num
    val dataHora = LocalDate.now().format() + "_" + LocalTime.now().format()
    val usuario = Config.user?.login ?: ""
    produto.usuarioCD = usuario + "_" + dataHora
    produto.salva()
    produto.expira()
    subView.updateProdutos()
    val pedido = subView.findPedido() ?: fail("Nota não encontrada")
    val produtosRestantes = pedido.produtos(EMarcaPedido.CD)
    if (produtosRestantes.isEmpty()) {
      imprimeEtiquetaEnt(produto)
    }
  }

  private fun imprimeEtiquetaEnt(produto: ProdutoPedidoVenda) {
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreview(impressora,
                                   DadosEtiquetaPedido(titulo = "Entregue",
                                                       usuario = produto.usuarioNameCD,
                                                       pedido = produto.ordno.toString(),
                                                       data = produto.dataCD,
                                                       hora = produto.horaCD,
                                                       local = produto.localizacao ?: ""))
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  val subView
    get() = viewModel.view.tabPedidoCD
}

interface ITabPedidoCD : ITabView {
  fun filtro(marca: EMarcaPedido): FiltroPedido
  fun updatePedidos(pedidos: List<PedidoVenda>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoPedidoVenda>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoPedidoVenda?
  fun findPedido(): PedidoVenda?
}