package br.com.astrosoft.produto.viewmodel.pedido

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EMarcaPedido
import br.com.astrosoft.produto.model.beans.FiltroPedido
import br.com.astrosoft.produto.model.beans.PedidoVenda
import br.com.astrosoft.produto.model.beans.ProdutoPedidoVenda
import java.time.LocalDate
import java.time.LocalTime

class TabPedidoCDViewModel(val viewModel: PedidoViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaPedido.CD)
    val Pedidos = PedidoVenda.find(filtro)
    subView.updatePedidos(Pedidos)
  }

  fun marcaEntProdutos(codigoBarra: String) = viewModel.exec {
    val produtoNF = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produtoNF.marca = EMarcaPedido.ENT.num
    val dataHora = LocalDate.now().format() + "_" + LocalTime.now().format()
    val usuario = Config.user?.login ?: ""
    produtoNF.usuarioCD = usuario + "_" + dataHora
    produtoNF.salva()
    produtoNF.expira()
    subView.updateProdutos()
    updateView()
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
}